package io.tokern.bastion.resources;

import io.dropwizard.auth.Auth;
import io.tokern.bastion.api.Query;
import io.tokern.bastion.api.User;
import io.tokern.bastion.db.QueryDAO;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/queries")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class QueryResource {
  private static final Logger logger = LoggerFactory.getLogger(QueryResource.class);
  private final Jdbi jdbi;

  public QueryResource(final Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @GET
  public List<Query> list(@Auth User principal) {
    return jdbi.withExtension(QueryDAO.class, dao -> dao.listByUser(principal.id, principal.orgId));
  }

  @GET
  @Path("{queryId}")
  public Query getQuery(@Auth User principal, @PathParam("queryId") final long queryId) {
    return jdbi.withExtension(QueryDAO.class, dao -> dao.getById(queryId, principal.orgId));
  }
}
