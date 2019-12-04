package io.tokern.bastion.core.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.tokern.bastion.api.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;

public class JWTAuthenticator implements Authenticator<String, User> {

  private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticator.class);

  private final Algorithm algorithm;
  private final JWTVerifier jwtVerifier;
  private final String issuer = "bastion";

  @Inject
  public JWTAuthenticator(JWTConfiguration configuration) {
    algorithm = Algorithm.HMAC256(configuration.getJwtSecret());

    jwtVerifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build();
  }

  @Override
  public Optional<User> authenticate(String token) throws AuthenticationException {
    DecodedJWT jwt = jwtVerifier.verify(token);
    LOGGER.info("Decoded JWT token");

    Claim id = jwt.getClaim("id");
    Claim email = jwt.getClaim("email");
    Claim name = jwt.getClaim("name");
    Claim orgId = jwt.getClaim("orgId");

    if (id == null || email == null || name == null || orgId == null) {
      LOGGER.warn("Failed JWT token verification");
      return Optional.empty();
    }

    return Optional.of(new User(id.asInt(), name.asString(), email.asString(), orgId.asInt()));
  }
}