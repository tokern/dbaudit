package io.tokern.bastion.resources;

import io.dropwizard.auth.Auth;
import io.tokern.bastion.api.Database;
import io.tokern.bastion.api.User;
import io.tokern.bastion.db.DatabaseDAO;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@Path("/databases")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "DBADMIN"})
public class DatabaseResource {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseResource.class);

  private final Jdbi jdbi;

  public DatabaseResource(final Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @PermitAll
  @GET
  public Database.DatabaseList list(@Auth User principal) {
    Database.DatabaseList list = new Database.DatabaseList(
        jdbi.withExtension(DatabaseDAO.class, dao -> dao.listByOrgId(principal.orgId)));
    logger.debug("Returning list of size: " + list.databases.size() + " for org: " + principal.orgId);
    return list;
  }

  @PermitAll
  @GET
  @Path("/drivers")
  public Database.DriverList drivers(@Auth User principal) {
    return new Database.DriverList(Arrays.asList(Database.Driver.values()));
  }

  @PermitAll
  @GET
  @Path("/{databaseId}")
  public Database getDatabase(@Auth User principal, @PathParam("databaseId") final int databaseId) {
    return jdbi.withExtension(DatabaseDAO.class, dao -> dao.getById(databaseId, principal.orgId));
  }

  @DELETE
  @Path("/{databaseId}")
  public Response deleteDatabase(@Auth User principal, @PathParam("databaseId") final int databaseId) {
    jdbi.useExtension(DatabaseDAO.class, dao -> dao.deleteById(databaseId, principal.orgId));
    return Response.ok("Database '" + databaseId + "' deleted").build();
  }

  @POST
  public Database createDatabase(@Auth User principal, @Valid @NotNull Database database) {
    Database updated = new Database(
        database.getId(), database.getName(), database.getJdbcUrl(),
        database.getUserName(), database.getPassword(), database.getDriverType(), principal.orgId);

    Long id = jdbi.withExtension(DatabaseDAO.class, dao -> dao.insert(updated));
    return jdbi.withExtension(DatabaseDAO.class, dao -> dao.getById(id, principal.orgId));
  }

  @PUT
  @Path("/{databaseId}")
  public Response updateDatabase(@Auth User principal,
                                 @PathParam("databaseId") final int databaseId,
                                 @Valid @NotNull Database.UpdateRequest request) {
    Database inDb = jdbi.withExtension(DatabaseDAO.class, dao -> dao.getById(databaseId, principal.orgId));
    if (inDb != null) {
      Database updated = new Database(
          inDb.getId(),
          request.getName() != null ? request.getName() : inDb.getName(),
          request.getJdbcUrl() != null ? request.getJdbcUrl() : inDb.getJdbcUrl(),
          request.getUserName() != null ? request.getUserName() : inDb.getJdbcUrl(),
          request.getPassword() != null ? request.getPassword() : inDb.getPassword(),
          request.getType() != null ? Database.Driver.valueOf(request.getType()) : inDb.getDriverType(),
          inDb.getOrgId()
      );
      jdbi.useExtension(DatabaseDAO.class, dao -> dao.update(updated));
      return Response.ok(updated).build();
    }

    return Response.status(Response.Status.NOT_FOUND).build();
  }
}
