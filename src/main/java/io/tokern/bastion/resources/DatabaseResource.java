package io.tokern.bastion.resources;

import io.dropwizard.auth.Auth;
import io.tokern.bastion.api.Database;
import io.tokern.bastion.api.Error;
import io.tokern.bastion.api.User;
import io.tokern.bastion.core.executor.Connections;
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
import java.sql.SQLException;
import java.util.Arrays;

@Path("/databases")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "DBADMIN"})
public class DatabaseResource {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseResource.class);

  private final Jdbi jdbi;
  private final DatabaseDAO dao;
  private final String encryptionSecret;
  private final Connections connections;

  public DatabaseResource(final Jdbi jdbi, String encryptionSecret, Connections connections) {
    this.jdbi = jdbi;
    this.dao = jdbi.onDemand(DatabaseDAO.class);
    this.encryptionSecret = encryptionSecret;
    this.connections = connections;
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
    return dao.getById(databaseId, principal.orgId);
  }

  @DELETE
  @Path("/{databaseId}")
  public Response deleteDatabase(@Auth User principal, @PathParam("databaseId") final int databaseId) {
    jdbi.useExtension(DatabaseDAO.class, dao -> dao.deleteById(databaseId, principal.orgId));
    return Response.ok("Database '" + databaseId + "' deleted").build();
  }

  @POST
  public Response createDatabase(@Auth User principal, @Valid @NotNull Database database) {
    if (dao.getByName(database.getName(), principal.orgId) == null) {
      Database updated = new Database(
          database.getId(), database.getName(), database.getJdbcUrl(),
          database.getUserName(),
          Database.encryptPassword(database.getPassword(), this.encryptionSecret),
          database.getDriverType(), principal.orgId);

      Long id = dao.insert(updated);
      Database created = dao.getById(id, principal.orgId);
      try {
        connections.addDatabase(created);
        return Response.ok().entity(created).build();
      } catch (SQLException exception) {
        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(new Error(exception.getMessage())).build();
      }
    } else {
      return Response
          .status(Response.Status.BAD_REQUEST)
          .entity(new Error(String.format("Database with name '%s' already exists", database.getName())))
          .build();
    }
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
          request.getPassword() != null ? Database.encryptPassword(request.getPassword(), this.encryptionSecret)
              : inDb.getPassword(),
          request.getType() != null ? Database.Driver.valueOf(request.getType()) : inDb.getDriverType(),
          inDb.getOrgId()
      );
      jdbi.useExtension(DatabaseDAO.class, dao -> dao.update(updated));
      try {
        connections.deleteDataSource(updated.getId());
        connections.addDatabase(updated);
        return Response.ok(updated).build();
      } catch (SQLException exception) {
        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(new Error(exception.getMessage())).build();
      }
    }
    return Response.status(Response.Status.NOT_FOUND).build();
  }
}
