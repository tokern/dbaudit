package io.tokern.bastion.core.auth;

import io.tokern.bastion.api.User;
import io.tokern.bastion.db.UserDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenManagerTest {
  @Mock
  UserDAO userDAO;
  User user = new User(1, "jwtUser", "jwtEmail", "passw0rd".getBytes(), User.SystemRoles.ADMIN, 1);

  @Test
  void testClaims() throws Exception {
    JwtTokenManager tokenManager = new JwtTokenManager("secret", 120);
    String token = tokenManager.generateToken(user);

    when(userDAO.getById(anyLong(), anyInt())).thenReturn(user);
    JwtAuthenticator authenticator = new JwtAuthenticator("secret", userDAO);
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
    JwtAuthenticator authenticator = new JwtAuthenticator("secret", userDAO);
    Optional verified = authenticator.authenticate(token);
    assertFalse(verified.isPresent());
  }
}