package io.tokern.bastion.resources;

import io.dropwizard.auth.Auth;
import io.tokern.bastion.api.Query;
import io.tokern.bastion.api.User;
import io.tokern.bastion.core.executor.Connections;
import io.tokern.bastion.core.executor.ThreadPool;
import io.tokern.bastion.db.QueryDAO;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/queries")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class QueryResource {
  private static final Logger logger = LoggerFactory.getLogger(QueryResource.class);
  private final QueryDAO queryDAO;
  private final Connections connections;
  private final ThreadPool threadPool;

  public QueryResource(QueryDAO queryDAO, Connections connections, ThreadPool threadPool) {
    this.queryDAO = queryDAO;
    this.connections = connections;
    this.threadPool = threadPool;
  }

  @GET
  public List<Query> list(@Auth User principal) {
    return queryDAO.listByUser(principal.id, principal.orgId);
  }

  @GET
  @Path("{queryId}")
  public Query getQuery(@Auth User principal, @PathParam("queryId") final long queryId) {
    return queryDAO.getById(queryId, principal.orgId);
  }

  @POST
  public Response createQuery(@Auth User principal, @Valid @NotNull Query query) {
    try {
      Long id = queryDAO.insert(query);
      Query saved = queryDAO.getById(id, principal.orgId);
      threadPool.getService().submit(new ThreadPool.Work(saved, queryDAO,
        connections.getDataSource(query.dbId).getConnection()));
      return Response.ok(saved).build();
    } catch (SQLException exception) {
      return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE)
          .entity(exception.getMessage()).build();
    }
 }
}
