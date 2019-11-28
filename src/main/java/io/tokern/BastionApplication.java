package io.tokern;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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
                    final Environment environment) {
        // TODO: implement application
    }

}
