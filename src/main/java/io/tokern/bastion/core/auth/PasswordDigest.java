package io.tokern.bastion.core.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.nio.charset.StandardCharsets;

public final class PasswordDigest {
  private final byte[] digest;
  public static final int cost = 6;

  private PasswordDigest(final byte[] digest) {
    this.digest = digest;
  }

  public byte[] getDigest() {
    return digest;
  }

  public boolean checkPassword(final byte[] passwordToCheck) {
    return BCrypt.verifyer().verify(passwordToCheck, digest).verified;
  }

  public static PasswordDigest fromDigest(final byte[] digest) {
    return new PasswordDigest(digest);
  }

  public static PasswordDigest generateFromPassword(final String password) {
    return PasswordDigest.fromDigest(BCrypt.withDefaults().hash(cost, password.getBytes(StandardCharsets.UTF_8)));
  }
}
