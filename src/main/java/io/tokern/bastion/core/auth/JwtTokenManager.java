package io.tokern.bastion.core.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import io.tokern.bastion.api.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtTokenManager {
  Logger logger = LoggerFactory.getLogger(JwtTokenManager.class);

  private final Algorithm algorithm;
  private final JWTVerifier jwtVerifier;
  private final String issuer = "bastion";

  public JwtTokenManager(final String secret) {
    algorithm = Algorithm.HMAC256(secret);

    jwtVerifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build();
  }

  public String generateToken(final User user) {
    String token = JWT.create()
        .withIssuer(issuer)
        .withClaim("id", user.id)
        .withClaim("email", user.email)
        .withClaim("name", user.name)
        .withClaim("systemRole", user.systemRole.name())
        .withClaim("orgId", user.orgId)
        .sign(algorithm);

    return token;
  }
}
