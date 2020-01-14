package io.tokern.bastion.api;

import java.security.Principal;

public class RefreshTokenUser implements Principal {
  public final User user;

  public RefreshTokenUser(User user) {
    this.user = user;
  }

  @Override
  public String getName() {
    return user.getName();
  }
}
