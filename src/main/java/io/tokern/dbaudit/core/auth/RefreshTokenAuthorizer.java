package io.tokern.dbaudit.core.auth;

import io.dropwizard.auth.Authorizer;
import io.tokern.dbaudit.api.RefreshTokenUser;
import io.tokern.dbaudit.api.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefreshTokenAuthorizer implements Authorizer<RefreshTokenUser> {
  Logger logger = LoggerFactory.getLogger(JwtAuthorizer.class);

  @Override
  public boolean authorize(RefreshTokenUser refreshTokenUser, String s) {
    User user = refreshTokenUser.user;
    logger.debug(String.format("Checking if user %s (%s) has role %s", user.name, user.systemRole.name(), s));
    return user.systemRole == User.SystemRoles.valueOf(s);
  }
}
