package io.tokern.bastion;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import io.tokern.bastion.core.Flyway.FlywayFactory;
import io.tokern.bastion.core.auth.JWTConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.*;

public class BastionConfiguration extends Configuration {
  @Valid
  @NotNull
  private DataSourceFactory database = new DataSourceFactory();

  @Valid
  @NotNull
  private FlywayFactory flywayFactory = new FlywayFactory();

  @Valid
  @NotNull

  private JWTConfiguration jwtConfiguration = new JWTConfiguration();

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

  @JsonProperty("jwt")
  public JWTConfiguration getJwtConfiguration() {
    return jwtConfiguration;
  }

  @JsonProperty("jwt")
  public void setJwtConfiguration(JWTConfiguration jwtConfiguration) {
    this.jwtConfiguration = jwtConfiguration;
  }
}
