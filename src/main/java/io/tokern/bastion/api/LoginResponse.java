package io.tokern.bastion.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
  public final String token;
  public final User user;

  @JsonCreator
  public LoginResponse(@JsonProperty("token") String token,
                       @JsonProperty("user") User user) {
    this.token = token;
    this.user = user;
  }
}
