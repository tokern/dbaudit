package io.tokern.bastion.core.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.tokern.bastion.api.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Cookie;
import java.util.Optional;

public class JWTAuthenticator implements Authenticator<Cookie, User> {

  private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticator.class);

  private final JwtTokenManager jwtTokenManager;

  @Inject
  public JWTAuthenticator(final JwtTokenManager jwtTokenManager) {
    this.jwtTokenManager = jwtTokenManager;
  }

  @Override
  public Optional<User> authenticate(final Cookie cookie) throws AuthenticationException {
    final String token = cookie.getValue();
    return Optional.of(jwtTokenManager.verifyToken(token));
  }
}