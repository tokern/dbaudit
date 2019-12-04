package io.tokern.bastion.core.auth;

import io.tokern.bastion.api.User;
import io.tokern.bastion.db.UserDAO;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenManagerTest {
  @Test
  void testClaims(@Mock Jdbi jdbi) throws Exception {
    User user = new User(1, "jwtUser", "jwtEmail", "passw0rd".getBytes(), User.SystemRoles.ADMIN, 1);
    JwtTokenManager tokenManager = new JwtTokenManager("secret");
    String token = tokenManager.generateToken(user);

    when(jdbi.withExtension(eq(UserDAO.class), any())).thenReturn(user);
    JwtAuthenticator authenticator = new JwtAuthenticator("secret", jdbi);
    User verified = authenticator.authenticate(token).get();

    assertEquals(user.id, verified.id);
    assertEquals(user.name, verified.name);
    assertEquals(user.email, verified.email);
    assertEquals(user.orgId, verified.orgId);
  }
}