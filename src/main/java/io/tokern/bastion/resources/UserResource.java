package io.tokern.bastion.resources;

import io.dropwizard.auth.Auth;
import io.tokern.bastion.api.LoginRequest;
import io.tokern.bastion.api.LoginResponse;
import io.tokern.bastion.api.User;
import io.tokern.bastion.core.auth.JwtTokenManager;
import io.tokern.bastion.db.UserDAO;
import org.jdbi.v3.core.Jdbi;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
  private final Jdbi jdbi;
  private final JwtTokenManager jwtTokenManager;

  public UserResource(Jdbi jdbi, JwtTokenManager jwtTokenManager) {
    this.jdbi = jdbi;
    this.jwtTokenManager = jwtTokenManager;
  }

  @PermitAll
  @GET
  @Path("/{userId}")
  public User getUser(@Auth final User principal,
                      @PathParam("userId") final int userId) {
    return jdbi.withExtension(UserDAO.class, dao -> dao.getById(userId));
  }

  @Path("/login")
  @POST
  public Response loginUser(@Valid final LoginRequest loginRequest) {
    User user = jdbi.withExtension(UserDAO.class, dao -> dao.getByEmail(loginRequest.email));

    if (user != null) {
      if (user.login(loginRequest.password)) {
        return Response.ok(new LoginResponse(jwtTokenManager.generateToken(user))).build();
      }
    }

    throw new WebApplicationException("Email or Password is incorrect!", Response.Status.UNAUTHORIZED);
  }
}
