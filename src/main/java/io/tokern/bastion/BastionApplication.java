package io.tokern.bastion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arteam.jdbi3.JdbiFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.tokern.bastion.api.Database;
import io.tokern.bastion.api.GitState;
import io.tokern.bastion.api.User;
import io.tokern.bastion.core.Flyway.FlywayBundle;
import io.tokern.bastion.core.Flyway.FlywayFactory;
import io.tokern.bastion.core.auth.JwtAuthenticator;
import io.tokern.bastion.core.auth.JwtAuthFilter;
import io.tokern.bastion.core.auth.JwtAuthorizer;
import io.tokern.bastion.core.auth.JwtTokenManager;
import io.tokern.bastion.core.executor.Connections;
import io.tokern.bastion.core.executor.RowSetModule;
import io.tokern.bastion.core.executor.ThreadPool;
import io.tokern.bastion.db.DatabaseDAO;
import io.tokern.bastion.db.QueryDAO;
import io.tokern.bastion.resources.*;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jdbi.v3.core.Jdbi;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Future;

public class BastionApplication extends Application<BastionConfiguration> {

    public static void main(final String[] args) throws Exception {
      new BastionApplication().run(args);
    }

    @Override
    public String getName() {
        return "Bastion";
    }

    @Override
    public void initialize(final Bootstrap<BastionConfiguration> bootstrap) {
      bootstrap.addBundle(new AssetsBundle("/frontend/assets/", "/", "index.html"));
      bootstrap.addBundle(new FlywayBundle<BastionConfiguration>() {
        @Override
        public DataSourceFactory getDataSourceFactory(BastionConfiguration configuration) {
          return configuration.getDataSourceFactory();
        }

        @Override
        public FlywayFactory getFlywayFactory(BastionConfiguration configuration) {
          return configuration.getFlywayFactory();
        }
      });
      bootstrap.setConfigurationSourceProvider(
          new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
              new EnvironmentVariableSubstitutor(false)
          )
      );
      bootstrap.getObjectMapper().registerModules(new RowSetModule());
    }

    private void addDatabases(Jdbi jdbi, Connections connections) {
      List<Database> databases = jdbi.withExtension(DatabaseDAO.class, DatabaseDAO::listAll);
      databases.forEach(database -> {
        try {
          connections.addDatabase(database);
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      });
    }

    @Override
    public void run(final BastionConfiguration configuration,
                    final Environment environment) throws IOException {
      InputStream stream =  getClass().getClassLoader().getResourceAsStream("git.properties");
      GitState gitState = new ObjectMapper().readValue(stream, GitState.class);

      System.setProperty("p6spy.config.appender", "com.p6spy.engine.spy.appender.Slf4JLogger");

      final JdbiFactory factory = new JdbiFactory();
      final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");

      final Connections connections = new Connections(environment.healthChecks(), environment.metrics(),
          configuration.getEncryptionSecret());
      environment.lifecycle().manage(connections);
      this.addDatabases(jdbi, connections);

      final ThreadPool threadPool = new ThreadPool();
      environment.lifecycle().manage(threadPool);

      environment.jersey().register(new Version(gitState));
      environment.jersey().register(new BootstrapResource(jdbi, configuration.getFeConfiguration(), gitState));

      final JwtTokenManager tokenManager = new JwtTokenManager(configuration.getJwtConfiguration().getJwtSecret(),
          configuration.getJwtConfiguration().getJwtExpirySeconds());
      final JwtAuthFilter authFilter = new JwtAuthFilter.Builder()
          .setCookieName(configuration.getJwtConfiguration().getCookieName())
          .setPrefix("BEARER")
          .setAuthenticator(new JwtAuthenticator(configuration.getJwtConfiguration().getJwtSecret(), jdbi))
          .setAuthorizer(new JwtAuthorizer())
          .buildAuthFilter();
      final Cache<Long, Future< ThreadPool.Result>> resultCache = CacheBuilder.newBuilder()
          .maximumSize(1000)
          .concurrencyLevel(5)
          .build();

      environment.jersey().register(new UserResource(jdbi, tokenManager));
      environment.jersey().register(new DatabaseResource(jdbi, configuration.getEncryptionSecret()));

      environment.jersey().register(new QueryResource(jdbi.onDemand(QueryDAO.class), jdbi.onDemand(DatabaseDAO.class),
          connections, threadPool, resultCache));

      environment.jersey().register(new AuthDynamicFeature(authFilter));
      environment.jersey().register(RolesAllowedDynamicFeature.class);
      //Required to use @Auth to inject a custom Principal type into your resource
      environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
    }
}
