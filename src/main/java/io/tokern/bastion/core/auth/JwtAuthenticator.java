package io.tokern.bastion.core.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.tokern.bastion.api.User;
import io.tokern.bastion.db.UserDAO;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;

public class JwtAuthenticator implements Authenticator<String, User> {

  private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticator.class);

  private final Algorithm algorithm;
  private final JWTVerifier jwtVerifier;
  private final String issuer = "bastion";
  private final Jdbi jdbi;

  @Inject
  public JwtAuthenticator(final String secret, final Jdbi jdbi) {
    algorithm = Algorithm.HMAC256(secret);

    jwtVerifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build();

    this.jdbi = jdbi;
  }

  @Override
  public Optional<User> authenticate(String token) throws AuthenticationException {
    DecodedJWT jwt = jwtVerifier.verify(token);
    LOGGER.info("Decoded JWT token");

    Claim id = jwt.getClaim("id");
    Claim email = jwt.getClaim("email");
    Claim name = jwt.getClaim("name");
    Claim systemRole = jwt.getClaim("systemRole");
    Claim orgId = jwt.getClaim("orgId");

    if (id == null || email == null || name == null || orgId == null) {
      LOGGER.warn("Failed JWT token verification");
      return Optional.empty();
    }
    User user = this.jdbi.withExtension(UserDAO.class, dao -> dao.getById(id.asInt()));

    if (user == null
        || !user.name.equals(name.asString())
        || !user.email.equals(email.asString())
        || !user.systemRole.name().equals(systemRole.asString())
        || user.orgId != orgId.asInt()
    ) {
      return Optional.empty();
    }

    return Optional.of(user);
  }
}