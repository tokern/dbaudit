package io.tokern.dbaudit.core.auth;

import io.dropwizard.auth.AuthFilter;
import io.tokern.dbaudit.api.RefreshTokenUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Priority(Priorities.AUTHENTICATION)
public class RefreshTokenAuthFilter extends AuthFilter<String, RefreshTokenUser> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RefreshTokenAuthFilter.class);

  private final String cookieName;

  private RefreshTokenAuthFilter(String cookieName) {
    this.cookieName = cookieName;
  }

  @Override
  public void filter(final ContainerRequestContext requestContext) {
    final Optional<String> optionalToken = getTokenFromCookie(requestContext);

    if (optionalToken.isPresent()) {
      LOGGER.info("Refresh Token found. Attempting to authenticate");
      if (!authenticate(requestContext, optionalToken.get(), "TOKEN")) {
        LOGGER.warn("Token authentication failed");
        throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
      }
    } else {
      LOGGER.warn("Token not found in cookie");
      throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
    }
  }

  private Optional<String> getTokenFromCookie(ContainerRequestContext requestContext) {
    final Map<String, Cookie> cookies = requestContext.getCookies();

    if (cookieName != null && cookies.containsKey(cookieName)) {
      final Cookie tokenCookie = cookies.get(cookieName);
      final String rawToken = tokenCookie.getValue();
      return Optional.of(rawToken);
    }

    return Optional.empty();
  }

  public static class Builder extends AuthFilterBuilder<String, RefreshTokenUser, RefreshTokenAuthFilter> {
    private String cookieName;

    public RefreshTokenAuthFilter.Builder setCookieName(String cookieName) {
      this.cookieName = cookieName;
      return this;
    }

    @Override
    protected RefreshTokenAuthFilter newInstance() {
      checkNotNull(cookieName, "cookieName is not set");
      return new RefreshTokenAuthFilter(cookieName);
    }
  }
}
