package io.tokern.bastion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arteam.jdbi3.JdbiFactory;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.tokern.bastion.api.GitState;
import io.tokern.bastion.core.Flyway.FlywayBundle;
import io.tokern.bastion.core.Flyway.FlywayFactory;
import io.tokern.bastion.resources.Version;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;

import java.io.IOException;
import java.io.InputStream;

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
    }

    final Flyway getFlyway(Environment environment,
                     BastionConfiguration configuration) {
      ManagedDataSource dataSource = configuration.getDataSourceFactory().build(environment.metrics(), "flyway");
      final Flyway flyway = configuration.getFlywayFactory().build(dataSource);
      return flyway;
    }

    @Override
    public void run(final BastionConfiguration configuration,
                    final Environment environment) throws IOException {
      InputStream stream =  getClass().getClassLoader().getResourceAsStream("git.properties");
      GitState gitState = new ObjectMapper().readValue(stream, GitState.class);

      final Flyway flyway = getFlyway(environment, configuration);
      flyway.migrate();
      flyway.clean();

      final JdbiFactory factory = new JdbiFactory();
      final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");

      environment.jersey().register(new Version(gitState));
    }
}
