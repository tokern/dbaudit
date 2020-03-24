package io.tokern.dbaudit.core.auth;

import io.dropwizard.auth.AuthFilter;
import io.tokern.dbaudit.api.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.Optional;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter extends AuthFilter<String, User> {

  private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthFilter.class);

  private JwtAuthFilter() {}

  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {
    final Optional<String> optionalToken = getTokenFromHeader(requestContext.getHeaders());

    if (optionalToken.isPresent()) {
      LOGGER.info("Token found. Attempting to authenticate");
      if (!authenticate(requestContext, optionalToken.get(), "TOKEN")) {
        LOGGER.warn("Token authentication failed");
        throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
      }
    } else {
      LOGGER.warn("Token not found in header");
      throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
    }
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

  public static class Builder extends AuthFilterBuilder<String, User, JwtAuthFilter> {
    @Override
    protected JwtAuthFilter newInstance() {
      return new JwtAuthFilter();
    }
  }
}
