package io.tokern.dbaudit.core.Flyway;

import io.dropwizard.Configuration;

public interface FlywayConfiguration<T extends Configuration> {
  FlywayFactory getFlywayFactory(T configuration);
}
