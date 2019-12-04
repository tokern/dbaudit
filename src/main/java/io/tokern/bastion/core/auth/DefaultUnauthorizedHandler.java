package io.tokern.bastion.core.auth;

import io.dropwizard.auth.UnauthorizedHandler;

import javax.ws.rs.core.Response;

public class DefaultUnauthorizedHandler implements UnauthorizedHandler {

  @Override
  public Response buildResponse(final String prefix, final String realm) {
    return Response.status(Response.Status.UNAUTHORIZED)
        .entity("You have to be logged in to access this resource.")
        .build();
  }
}
