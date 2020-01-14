package io.tokern.bastion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arteam.jdbi3.JdbiFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.*;
import io.dropwizard.auth.chained.ChainedAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.tokern.bastion.api.Database;
import io.tokern.bastion.api.GitState;
import io.tokern.bastion.api.RefreshTokenUser;
import io.tokern.bastion.api.User;
import io.tokern.bastion.core.Flyway.FlywayBundle;
import io.tokern.bastion.core.Flyway.FlywayFactory;
import io.tokern.bastion.core.auth.*;
import io.tokern.bastion.core.executor.Connections;
import io.tokern.bastion.core.executor.RowSetModule;
import io.tokern.bastion.core.executor.ThreadPool;
import io.tokern.bastion.db.DatabaseDAO;
import io.tokern.bastion.db.QueryDAO;
import io.tokern.bastion.db.RefreshTokenDao;
import io.tokern.bastion.db.UserDAO;
import io.tokern.bastion.resources.*;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
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
      final UserDAO userDAO = jdbi.onDemand(UserDAO.class);
      final RefreshTokenDao refreshTokenDao = jdbi.onDemand(RefreshTokenDao.class);

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
      final JwtAuthFilter jwtAuthFilter = new JwtAuthFilter.Builder()
          .setPrefix("BEARER")
          .setAuthenticator(new JwtAuthenticator(configuration.getJwtConfiguration().getJwtSecret(), userDAO))
          .setAuthorizer(new JwtAuthorizer())
          .buildAuthFilter();

      final RefreshTokenManager refreshTokenManager = new RefreshTokenManager(refreshTokenDao,
          configuration.getJwtConfiguration().getRefreshTokenExpirySeconds(),
          configuration.getJwtConfiguration().getCookieName());
      final RefreshTokenAuthFilter refreshTokenAuthFilter = new RefreshTokenAuthFilter.Builder()
          .setCookieName(configuration.getJwtConfiguration().getCookieName())
          .setAuthenticator(new RefreshTokenAuthenticator(refreshTokenDao, userDAO))
          .setAuthorizer(new RefreshTokenAuthorizer())
          .buildAuthFilter();

      final Cache<Long, Future< ThreadPool.Result>> resultCache = CacheBuilder.newBuilder()
          .maximumSize(1000)
          .concurrencyLevel(5)
          .build();

      environment.jersey().register(new UserResource(jdbi, tokenManager, refreshTokenManager));
      environment.jersey().register(new DatabaseResource(jdbi, configuration.getEncryptionSecret(), connections));

      environment.jersey().register(new QueryResource(jdbi.onDemand(QueryDAO.class), jdbi.onDemand(DatabaseDAO.class),
          connections, threadPool, resultCache));

      final PolymorphicAuthDynamicFeature feature = new PolymorphicAuthDynamicFeature<>(
          ImmutableMap.of(
              User.class, jwtAuthFilter,
              RefreshTokenUser.class, refreshTokenAuthFilter));
      final AbstractBinder binder = new PolymorphicAuthValueFactoryProvider.Binder<>(
          ImmutableSet.of(User.class, RefreshTokenUser.class));

      environment.jersey().register(RolesAllowedDynamicFeature.class);
      environment.jersey().register(feature);
      environment.jersey().register(binder);
    }
}
