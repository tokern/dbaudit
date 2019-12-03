package io.tokern.bastion.core.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter<P extends Principal> extends AuthFilter<DecodedJWT, P> {

  private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthFilter.class);

  private final JwtTokenManager tokenManager;
  private final String cookieName;
  private final String prefix = "BEARER";

  private JwtAuthFilter(JwtTokenManager tokenManager, String cookieName) {
    this.tokenManager = tokenManager;
    this.cookieName = cookieName;
  }

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {
    final Optional<String> optionalToken = getTokenFromCookieOrHeader(requestContext);

    if (optionalToken.isPresent()) {
      try {
        final DecodedJWT jwt = verifyToken(optionalToken.get());
        final Optional<P> principal = authenticator.authenticate(jwt);

        if (principal.isPresent()) {
          requestContext.setSecurityContext(new SecurityContext() {

            @Override
            public Principal getUserPrincipal() {
              return principal.get();
            }

            @Override
            public boolean isUserInRole(String role) {
              return authorizer.authorize(principal.get(), role);
            }

            @Override
            public boolean isSecure() {
              return requestContext.getSecurityContext().isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
              return SecurityContext.BASIC_AUTH;
            }

          });
          return;
        }
      } catch (AuthenticationException ex) {
        LOGGER.warn("Error authenticating credentials", ex);
        throw new InternalServerErrorException();
      }
    }

    throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
  }

  private DecodedJWT verifyToken(String rawToken) throws AuthenticationException {
    return tokenManager.verifyToken(rawToken);
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

  public static class Builder<P extends Principal> extends AuthFilterBuilder<DecodedJWT, P, JwtAuthFilter<P>> {

    private JwtTokenManager tokenManager;
    private String cookieName;

    public Builder<P> setJwtTokenManager(JwtTokenManager tokenManager) {
      this.tokenManager = tokenManager;
      return this;
    }

    public Builder<P> setCookieName(String cookieName) {
      this.cookieName = cookieName;
      return this;
    }

    @Override
    protected JwtAuthFilter<P> newInstance() {
      checkNotNull(tokenManager, "JwtTokenManager is not set");
      return new JwtAuthFilter<>(tokenManager, cookieName);
    }
  }
}
