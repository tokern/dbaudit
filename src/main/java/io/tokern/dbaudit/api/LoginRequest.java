package io.tokern.dbaudit.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {
  public final String email;
  public final String password;

  @JsonCreator
  public LoginRequest(@JsonProperty("email") String email, @JsonProperty("password") String password) {
    this.email = email;
    this.password = password;
  }
}
