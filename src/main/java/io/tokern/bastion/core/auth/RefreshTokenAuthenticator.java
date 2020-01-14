package io.tokern.bastion.core.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.tokern.bastion.api.RefreshToken;
import io.tokern.bastion.api.RefreshTokenUser;
import io.tokern.bastion.api.User;
import io.tokern.bastion.db.RefreshTokenDao;
import io.tokern.bastion.db.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

public class RefreshTokenAuthenticator implements Authenticator<String, RefreshTokenUser> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RefreshTokenAuthFilter.class);
  private final RefreshTokenDao refreshTokenDao;
  private final UserDAO userDAO;

  @Inject
  public RefreshTokenAuthenticator(RefreshTokenDao refreshTokenDao, UserDAO userDAO) {
    this.refreshTokenDao = refreshTokenDao;
    this.userDAO = userDAO;
  }

  @Override
  public Optional<RefreshTokenUser> authenticate(String token) throws AuthenticationException {
    RefreshToken refreshToken = this.refreshTokenDao.getByToken(token);

    if (refreshToken != null && !refreshToken.forceInvalidated
        && refreshToken.expiresAt.isAfter(ZonedDateTime.now(ZoneOffset.UTC))) {
      LOGGER.debug("Refresh Token is valid");
      User user = this.userDAO.getById(refreshToken.userId, refreshToken.orgId);
      if (user != null) {
        LOGGER.debug(String.format("User %s found for refresh token", user.email));
        return Optional.of(new RefreshTokenUser(user));
      }
    }

    LOGGER.info("Rejecting authentication for invalid refresh token");
    return Optional.empty();
  }
}
