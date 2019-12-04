package io.tokern.bastion.core.auth;

import io.dropwizard.auth.Authorizer;
import io.tokern.bastion.api.User;

public class JwtAuthorizer implements Authorizer<User> {

  @Override
  public boolean authorize(User user, String s) {
    return user.systemRole == User.SystemRoles.valueOf(s);
  }
}
