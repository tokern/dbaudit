package io.tokern.dbaudit.resources;

import io.dropwizard.auth.Auth;
import io.tokern.dbaudit.api.LoginRequest;
import io.tokern.dbaudit.api.LoginResponse;
import io.tokern.dbaudit.api.RefreshTokenUser;
import io.tokern.dbaudit.api.User;
import io.tokern.dbaudit.core.auth.JwtTokenManager;
import io.tokern.dbaudit.core.auth.PasswordDigest;
import io.tokern.dbaudit.core.auth.RefreshTokenManager;
import io.tokern.dbaudit.db.UserDAO;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
  private static final Logger logger = LoggerFactory.getLogger(UserResource.class);

  private final Jdbi jdbi;
  private final JwtTokenManager jwtTokenManager;
  private final RefreshTokenManager refreshTokenManager;

  public UserResource(Jdbi jdbi, JwtTokenManager jwtTokenManager, RefreshTokenManager refreshTokenManager) {
    this.jdbi = jdbi;
    this.jwtTokenManager = jwtTokenManager;
    this.refreshTokenManager = refreshTokenManager;
  }

  @RolesAllowed("ADMIN")
  @GET
  public User.UserList list(@Auth User principal) {
    User.UserList list = new User.UserList(
        jdbi.withExtension(UserDAO.class, dao -> dao.listByOrg(principal.orgId)));
    logger.debug("Returning list of size: " + list.users.size() + " for org: " + principal.orgId);
    return list;
  }

  @PermitAll
  @GET
  @Path("/me")
  public User getUser(@Auth final User principal) {
    return principal;
  }

  @RolesAllowed("ADMIN")
  @GET
  @Path("/{userId}")
  public User getUser(@Auth final User principal,
                      @PathParam("userId") final int userId) {
    return jdbi.withExtension(UserDAO.class, dao -> dao.getById(userId, principal.orgId));
  }

  @Path("/login")
  @POST
  public Response loginUser(@Valid final LoginRequest loginRequest) {
    User user = jdbi.withExtension(UserDAO.class, dao -> dao.getByEmail(loginRequest.email));

    if (user != null) {
      if (user.login(loginRequest.password)) {
        return Response.ok(
            new LoginResponse(jwtTokenManager.generateToken(user), user))
            .cookie(refreshTokenManager.generateCookie(user))
            .build();
      }
    }

    throw new WebApplicationException("Email or Password is incorrect!", Response.Status.UNAUTHORIZED);
  }

  @Path("/logout")
  @GET
  public Response logOutUser(@Auth final User principal) {
    refreshTokenManager.invalidate(principal);
    return Response.ok().build();
  }

  @RolesAllowed("ADMIN")
  @POST
  public User createUser(@Auth final User admin, @Valid final User.Request request) {
    User newUser = new User(request.name, request.email,
        PasswordDigest.generateFromPassword(request.password).getDigest(),
        User.SystemRoles.valueOf(request.systemRole),
        admin.orgId);

    jdbi.useExtension(UserDAO.class, dao-> dao.insert(newUser));

    return jdbi.withExtension(UserDAO.class, dao -> dao.getByEmail(newUser.email));
  }

  @PermitAll
  @Path("/refreshJWT")
  @GET
  public Response refreshJWT(@Auth final RefreshTokenUser principal) {
    return Response.ok(new LoginResponse(jwtTokenManager.generateToken(principal.user), principal.user)).build();
  }

  @RolesAllowed("ADMIN")
  @Path("/{userId}")
  @PUT
  public Response updateUser(@Auth final User admin,
                             @PathParam("userId") final long userId,
                             @Valid final User.Request request) {
    User user = jdbi.withExtension(UserDAO.class, dao -> dao.getById(userId, admin.orgId));

    User updatedUser = new User(
        user.id,
        request.name == null ? user.name : request.name,
        request.email == null ? user.email : request.email,
        request.password == null ? user.passwordHash :
            PasswordDigest.generateFromPassword(request.password).getDigest(),
        request.systemRole == null ? user.systemRole : User.SystemRoles.valueOf(request.systemRole),
        user.orgId
    );

    jdbi.useExtension(UserDAO.class, dao-> dao.update(updatedUser));
    return Response.ok(jdbi.withExtension(UserDAO.class, dao -> dao.getById(user.id, admin.orgId))).build();
  }

  @PermitAll
  @PUT
  @Path("/changePassword")
  public Response changePassword(@Auth final User principal,
                             @Valid final User.PasswordChange request) {
    if (principal.login(request.currentPassword)) {
      User updated = new User(
          principal.id,
          principal.name,
          principal.email,
          PasswordDigest.generateFromPassword(request.newPassword).getDigest(),
          principal.systemRole,
          principal.orgId
      );

      jdbi.useExtension(UserDAO.class, dao-> dao.update(updated));
      return Response.ok("Password changed successfully").build();
    }
    throw new WebApplicationException("Current Password is incorrect!", Response.Status.UNAUTHORIZED);
  }
}
