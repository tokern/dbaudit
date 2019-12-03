package io.tokern.bastion.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
  public final String token;

  @JsonCreator
  public LoginResponse(@JsonProperty("token") String token) {
    this.token = token;
  }
}
