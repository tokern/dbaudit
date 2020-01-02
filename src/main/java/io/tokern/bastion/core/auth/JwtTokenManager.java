package io.tokern.bastion.core.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.tokern.bastion.api.User;

import java.time.Instant;
import java.util.Date;

public class JwtTokenManager {
  private final Algorithm algorithm;
  private final String issuer = "bastion";
  private final int expirySeconds;

  public JwtTokenManager(final String secret, int expirySeconds) {
    this.expirySeconds = expirySeconds;
    this.algorithm = Algorithm.HMAC256(secret);
  }

  public String generateToken(final User user) {
    return this.generateToken(user,
          Date.from(Instant.now().plusSeconds(this.expirySeconds)),
          Date.from(Instant.now()));
  }

  public String generateToken(final User user, Date expiresAt, Date issuedAt) {
    return JWT.create()
        .withIssuer(issuer)
        .withExpiresAt(expiresAt)
        .withIssuedAt(issuedAt)
        .withClaim("id", user.id)
        .withClaim("email", user.email)
        .withClaim("name", user.name)
        .withClaim("systemRole", user.systemRole.name())
        .withClaim("orgId", user.orgId)
        .sign(algorithm);
  }
}
