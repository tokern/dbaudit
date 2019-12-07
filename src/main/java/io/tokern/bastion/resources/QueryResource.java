package io.tokern.bastion.resources;

import com.google.common.cache.Cache;
import io.dropwizard.auth.Auth;
import io.tokern.bastion.api.Query;
import io.tokern.bastion.api.User;
import io.tokern.bastion.core.executor.Connections;
import io.tokern.bastion.core.executor.ThreadPool;
import io.tokern.bastion.db.QueryDAO;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Path("/queries")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class QueryResource {
  private static final Logger logger = LoggerFactory.getLogger(QueryResource.class);
  private final QueryDAO queryDAO;
  private final Connections connections;
  private final ThreadPool threadPool;
  private final Cache<Long, Future<ThreadPool.Result>> resultCache;

  public QueryResource(QueryDAO queryDAO, Connections connections, ThreadPool threadPool,
                       Cache<Long, Future<ThreadPool.Result>> resultCache) {
    this.queryDAO = queryDAO;
    this.connections = connections;
    this.threadPool = threadPool;
    this.resultCache = resultCache;
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

  @GET
  @Path("{queryId}/results")
  public Response getResults(@Auth User principal, @PathParam("queryId") final long queryId) {
    Query query = queryDAO.getById(queryId, principal.orgId);
    if (query != null) {
      if (query.state == Query.State.WAITING || query.state == Query.State.RUNNING) {
        return Response.status(202).entity(
            String.format("Query %d is in %s state", query.id, query.state.name())).build();
      } else {
        Future<ThreadPool.Result> future = resultCache.getIfPresent(query.id);
        ThreadPool.Result result = null;
        try {
          result = future.get();
        } catch (InterruptedException | ExecutionException exception) {
          logger.warn(String.format("Exception when getting result for %d", query.id), exception);
        }

        int responseCode = 0;
        Object responseObject = null;

        if (query.state == Query.State.ERROR) {
          responseCode = 400;
          responseObject = result != null ? result.throwable : "Query had an ERROR but results are not available";
        } else {
          responseCode = 200;
          responseObject = result != null ? result.resultSet : "Query succeeded but results are not available";
        }

        return Response.status(responseCode).entity(responseObject).build();
      }
    }
    return Response.status(404).entity(String.format("Query %d not found.", queryId)).build();
  }

  @POST
  public Response createQuery(@Auth User principal, @Valid @NotNull Query query) {
    try {
      Long id = queryDAO.insert(query);
      Query saved = queryDAO.getById(id, principal.orgId);
      Future<ThreadPool.Result> future = threadPool.getService().submit(
          new ThreadPool.Work(saved, queryDAO, connections.getDataSource(query.dbId).getConnection()));
      resultCache.put(id, future);
      return Response.ok(saved).build();
    } catch (SQLException exception) {
      return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE)
          .entity(exception.getMessage()).build();
    }
  }
}
