package io.tokern.bastion.core.auth;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.tokern.bastion.api.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;

public class JWTAuthenticator implements Authenticator<DecodedJWT, User> {

  private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticator.class);

  @Inject
  public JWTAuthenticator() {}

  @Override
  public Optional<User> authenticate(final DecodedJWT jwt) throws AuthenticationException {
    Claim id = jwt.getClaim("id");
    Claim email = jwt.getClaim("email");
    Claim name = jwt.getClaim("name");
    Claim orgId = jwt.getClaim("orgId");

    return Optional.of(new User(id.asInt(), name.asString(), email.asString(), orgId.asInt()));
  }
}