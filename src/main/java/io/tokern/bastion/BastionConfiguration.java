package io.tokern.bastion;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import io.tokern.bastion.core.Flyway.FlywayFactory;
import org.hibernate.validator.constraints.*;

import javax.validation.Valid;
import javax.validation.constraints.*;

public class BastionConfiguration extends Configuration {
  @Valid
  @NotNull
  private DataSourceFactory database = new DataSourceFactory();

  @Valid
  @NotNull
  private FlywayFactory flywayFactory = new FlywayFactory();

  @JsonProperty("database")
  public void setDataSourceFactory(DataSourceFactory factory) {
    this.database = factory;
  }

  @JsonProperty("database")
  public DataSourceFactory getDataSourceFactory() {
    return database;
  }

  @JsonProperty("flyway")
  public void setFlywayFactory(FlywayFactory flywayFactory) {
    this.flywayFactory = flywayFactory;
  }

  @JsonProperty("flyway")
  public FlywayFactory getFlywayFactory() {
    return flywayFactory;
  }
}
