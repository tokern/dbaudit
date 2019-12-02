package io.tokern.bastion.core.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;

public final class PasswordDigest {
  private final char[] digest;
  public static final int cost = 6;

  private PasswordDigest(final char[] digest) {
    this.digest = digest;
  }

  public char[] getDigest() {
    return digest;
  }

  public boolean checkPassword(final char[] passwordToCheck) {
    return BCrypt.verifyer().verify(passwordToCheck, digest).verified;
  }

  public static PasswordDigest fromDigest(final char[] digest) {
    return new PasswordDigest(digest);
  }

  public static PasswordDigest generateFromPassword(final String password) {
    return PasswordDigest.fromDigest(BCrypt.withDefaults().hashToChar(cost, password.toCharArray()));
  }
}
