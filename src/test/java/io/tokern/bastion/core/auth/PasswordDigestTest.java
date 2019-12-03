package io.tokern.bastion.core.auth;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class PasswordDigestTest {
  @Test
  void generateHash() {
    PasswordDigest digest = PasswordDigest.generateFromPassword("passw0rd");
    assertNotEquals("passw0rd", digest.getDigest().toString());
  }

  @Test
  void successfulVerification() {
    PasswordDigest digest = PasswordDigest.generateFromPassword("passw0rd");
    assertTrue(digest.checkPassword("passw0rd".getBytes(StandardCharsets.UTF_8)));
  }

  @Test
  void failedVerification() {
    PasswordDigest digest = PasswordDigest.generateFromPassword("passw0rd");
    assertFalse(digest.checkPassword("password".getBytes(StandardCharsets.UTF_8)));
  }
}