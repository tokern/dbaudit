package io.tokern.bastion.api;

import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

import javax.validation.constraints.NotNull;
import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;

public class RefreshToken {
  public final int id;

  @NotNull
  public final String token;

  @NotNull
  public final int userId;

  @NotNull
  public final int orgId;

  @NotNull
  public final ZonedDateTime createdAt;

  @NotNull
  public final ZonedDateTime expiresAt;

  public final boolean forceInvalidated;

  private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
  private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

  @JdbiConstructor
  public RefreshToken(int id, String token, int userId, int orgId,
                      ZonedDateTime createdAt, ZonedDateTime expiresAt, boolean forceInvalidated) {
    this.id = id;
    this.token = token;
    this.userId = userId;
    this.orgId = orgId;
    this.createdAt = createdAt;
    this.expiresAt = expiresAt;
    this.forceInvalidated = forceInvalidated;
  }

  public RefreshToken(String token, int userId, int orgId, ZonedDateTime createdAt, ZonedDateTime expiresAt,
                      boolean forceInvalidated) {
    this(0, token, userId, orgId, createdAt, expiresAt, forceInvalidated);
  }

  public static String generateNewToken() {
    byte[] randomBytes = new byte[24];
    secureRandom.nextBytes(randomBytes);
    return base64Encoder.encodeToString(randomBytes);
  }
}
