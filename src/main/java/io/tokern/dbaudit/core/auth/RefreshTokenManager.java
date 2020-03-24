package io.tokern.dbaudit.core.auth;

import io.tokern.dbaudit.api.RefreshToken;
import io.tokern.dbaudit.api.User;
import io.tokern.dbaudit.db.RefreshTokenDao;

import javax.ws.rs.core.NewCookie;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class RefreshTokenManager {
  private final RefreshTokenDao refreshTokenDao;
  private final int refreshTokenExpirySeconds;
  private final String cookieName;

  public RefreshTokenManager(RefreshTokenDao refreshTokenDao, int refreshTokenExpirySeconds, String cookieName) {
    this.refreshTokenDao = refreshTokenDao;
    this.refreshTokenExpirySeconds = refreshTokenExpirySeconds;
    this.cookieName = cookieName;
  }

  public NewCookie generateCookie(User user) {
    ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
    ZonedDateTime expiry = now.plusSeconds(this.refreshTokenExpirySeconds);
    RefreshToken refreshToken = new RefreshToken(RefreshToken.generateNewToken(), user.id, user.orgId,
        now, expiry, false);
    refreshTokenDao.insert(refreshToken);

    return new NewCookie(this.cookieName, refreshToken.token);
  }

  public void invalidate(User user) {
    refreshTokenDao.updateForceInvalidateByUserOrgId(true, user.id, user.orgId);
  }
}
