package io.tokern.bastion.core.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthenticationException;
import io.tokern.bastion.api.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter extends AuthFilter<String, User> {

  private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthFilter.class);

  private final String cookieName;

  private JwtAuthFilter(String cookieName) {
    this.cookieName = cookieName;
  }

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {
    final Optional<String> optionalToken = getTokenFromCookieOrHeader(requestContext);

    if (optionalToken.isPresent()) {
      LOGGER.info("Token found. Attempting to authenticate");
      if (!authenticate(requestContext, optionalToken.get(), "TOKEN")) {
        LOGGER.warn("Token authentication failed");
        throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
      }
    }
  }

  private Optional<String> getTokenFromCookieOrHeader(ContainerRequestContext requestContext) {
    final Optional<String> headerToken = getTokenFromHeader(requestContext.getHeaders());
    return headerToken.isPresent() ? headerToken : getTokenFromCookie(requestContext);
  }

  private Optional<String> getTokenFromHeader(MultivaluedMap<String, String> headers) {
    final String header = headers.getFirst(AUTHORIZATION);
    if (header != null) {
      int space = header.indexOf(' ');
      if (space > 0) {
        final String method = header.substring(0, space);
        if (prefix.equalsIgnoreCase(method)) {
          final String rawToken = header.substring(space + 1);
          return Optional.of(rawToken);
        }
      }
    }

    return Optional.empty();
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

  public static class Builder extends AuthFilterBuilder<String, User, JwtAuthFilter> {
    private String cookieName;

    public Builder setCookieName(String cookieName) {
      this.cookieName = cookieName;
      return this;
    }

    @Override
    protected JwtAuthFilter newInstance() {
      checkNotNull(cookieName, "cookieName is not set");
      return new JwtAuthFilter(cookieName);
    }
  }
}
