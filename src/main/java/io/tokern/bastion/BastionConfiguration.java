package io.tokern.bastion;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import io.tokern.bastion.core.FEConfiguration;
import io.tokern.bastion.core.Flyway.FlywayFactory;
import io.tokern.bastion.core.auth.JwtConfiguration;

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
  private JwtConfiguration jwtConfiguration = new JwtConfiguration();

  @Valid
  @NotNull
  private FEConfiguration feConfiguration = new FEConfiguration();

  @Valid
  @NotNull
  private String encryptionSecret;

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
  public JwtConfiguration getJwtConfiguration() {
    return jwtConfiguration;
  }

  @JsonProperty("jwt")
  public void setJwtConfiguration(JwtConfiguration jwtConfiguration) {
    this.jwtConfiguration = jwtConfiguration;
  }

  @JsonProperty("fe")
  public FEConfiguration getFeConfiguration() {
    return feConfiguration;
  }

  @JsonProperty("fe")
  public void setFeConfiguration(FEConfiguration feConfiguration) {
    this.feConfiguration = feConfiguration;
  }

  @JsonProperty("encryptionSecret")
  public String getEncryptionSecret() {
    return encryptionSecret;
  }

  @JsonProperty("encryptionSecret")
  public void setEncryptionSecret(String encryptionSecret) {
    this.encryptionSecret = encryptionSecret;
  }
}
