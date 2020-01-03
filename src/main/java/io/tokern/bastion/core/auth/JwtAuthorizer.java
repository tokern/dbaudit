package io.tokern.bastion.core.auth;

import io.dropwizard.auth.Authorizer;
import io.tokern.bastion.api.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtAuthorizer implements Authorizer<User> {
  Logger logger = LoggerFactory.getLogger(JwtAuthorizer.class);

  @Override
  public boolean authorize(User user, String s) {
    logger.debug(String.format("Checking if user %s (%s) has role %s", user.name, user.systemRole.name(), s));
    return user.systemRole == User.SystemRoles.valueOf(s);
  }
}
