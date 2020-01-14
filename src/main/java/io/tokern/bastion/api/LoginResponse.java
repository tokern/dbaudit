package io.tokern.bastion.api;

public class LoginResponse {
  public final String token;
  public final User user;

  public LoginResponse(String token, User user) {
    this.token = token;
    this.user = user;
  }
}
