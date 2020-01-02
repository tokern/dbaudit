package io.tokern.bastion.core.auth;

import io.tokern.bastion.api.User;
import io.tokern.bastion.db.UserDAO;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenManagerTest {
  @Mock
  Jdbi jdbi;
  User user = new User(1, "jwtUser", "jwtEmail", "passw0rd".getBytes(), User.SystemRoles.ADMIN, 1);

  @Test
  void testClaims() throws Exception {
    JwtTokenManager tokenManager = new JwtTokenManager("secret", 120);
    String token = tokenManager.generateToken(user);

    when(jdbi.withExtension(eq(UserDAO.class), any())).thenReturn(user);
    JwtAuthenticator authenticator = new JwtAuthenticator("secret", jdbi);
    User verified = authenticator.authenticate(token).get();

    assertEquals(user.id, verified.id);
    assertEquals(user.name, verified.name);
    assertEquals(user.email, verified.email);
    assertEquals(user.orgId, verified.orgId);
  }

  @Test
  void testExpiry() {
    JwtTokenManager tokenManager = new JwtTokenManager("secret", 120);
    Instant now = Instant.now();

    String token = tokenManager.generateToken(user, Date.from(now.minusSeconds(300)), Date.from(now.minusSeconds(100)));
    JwtAuthenticator authenticator = new JwtAuthenticator("secret", jdbi);
    Optional verified = authenticator.authenticate(token);
    assertFalse(verified.isPresent());
  }
}