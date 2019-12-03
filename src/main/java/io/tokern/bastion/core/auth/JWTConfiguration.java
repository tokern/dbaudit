package io.tokern.bastion.core.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JWTConfiguration {
  private String jwtSecret = "secret";
  private int jwtExpirySeconds = 3600;
  private String cookieName = "jwtTokern";

  @JsonProperty
  public String getJwtSecret() {
    return jwtSecret;
  }

  public void setJwtSecret(final String jwtSecret) {
    this.jwtSecret = jwtSecret;
  }

  @JsonProperty
  public int getJwtExpirySeconds() {
    return jwtExpirySeconds;
  }

  public void setJwtExpirySeconds(final int jwtExpirySeconds) {
    this.jwtExpirySeconds = jwtExpirySeconds;
  }

  @JsonProperty
  public String getCookieName() {
    return cookieName;
  }

  public void setCookieName(final String cookieName) {
    this.cookieName = cookieName;
  }
}
