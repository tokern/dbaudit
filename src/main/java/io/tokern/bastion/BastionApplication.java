package io.tokern.bastion;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.tokern.bastion.api.GitState;
import io.tokern.bastion.resources.Version;

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
    }

    @Override
    public void run(final BastionConfiguration configuration,
                    final Environment environment) throws IOException {
      InputStream stream =  getClass().getClassLoader().getResourceAsStream("git.properties");
      GitState gitState = new ObjectMapper().readValue(stream, GitState.class);

      environment.jersey().register(new Version(gitState));
    }
}
