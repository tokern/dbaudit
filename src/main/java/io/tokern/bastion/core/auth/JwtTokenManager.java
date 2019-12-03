package io.tokern.bastion.core.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.auth.AuthenticationException;
import io.tokern.bastion.api.User;

public class JwtTokenManager {

  private final Algorithm algorithm;
  private final JWTVerifier jwtVerifier;
  private final String cookieName;

  public JwtTokenManager(final JWTConfiguration configuration) {
    algorithm = Algorithm.HMAC256(configuration.getJwtSecret());

    jwtVerifier = JWT.require(algorithm)
        .withIssuer("bastion")
        .build();

    cookieName = configuration.getCookieName();
  }

  @SuppressWarnings("unchecked")
  public String generateToken(final User user) {
    String token = JWT.create()
        .withClaim("id", user.id)
        .withClaim("email", user.email)
        .withClaim("name", user.name)
        .withClaim("orgId", user.orgId)
        .sign(algorithm);

    return token;
  }

  public User verifyToken(String token) throws AuthenticationException {
    DecodedJWT jwt = jwtVerifier.verify(token);
    Claim id = jwt.getClaim("id");
    Claim email = jwt.getClaim("email");
    Claim name = jwt.getClaim("name");
    Claim orgId = jwt.getClaim("orgId");

    if (id == null || email == null || name == null || orgId == null) {
      throw new AuthenticationException("JWT token could not be verified");
    }

    return new User(id.asInt(), name.asString(), email.asString(), orgId.asInt());
  }
}
