package io.tokern.bastion.resources;

import com.codahale.metrics.annotation.Timed;
import io.tokern.bastion.api.BootstrapResponse;
import io.tokern.bastion.api.GitState;
import io.tokern.bastion.api.Organization;
import io.tokern.bastion.api.Register;
import io.tokern.bastion.core.FEConfiguration;
import io.tokern.bastion.db.OrganizationDAO;
import io.tokern.bastion.db.RegisterDAO;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/bootstrap")
public class BootstrapResource {
  private final Jdbi jdbi;
  private final FEConfiguration feConfiguration;
  private final GitState gitState;

  public BootstrapResource(Jdbi jdbi, FEConfiguration feConfiguration, GitState gitState) {
    this.jdbi = jdbi;
    this.feConfiguration = feConfiguration;
    this.gitState = gitState;
  }

  @GET
  @Timed
  public Response bootstrap() {

    BootstrapResponse response = new BootstrapResponse(
        jdbi.withExtension(OrganizationDAO.class, dao -> dao.list()).isEmpty(),
        null, new BootstrapResponse.Config(feConfiguration), gitState.buildVersion
    );

    return Response.ok(response).build();
  }

  @POST
  @Timed
  @Path("/register")
  public Response newRegistration(Register register) {
    RegisterDAO.insert(jdbi, register);
    Organization organization = jdbi.withExtension(OrganizationDAO.class, dao -> dao.getByName(register.orgName));

    if (organization != null) {
      return Response.ok(organization).build();
    } else {
      return Response.status(Response.Status.FORBIDDEN).entity(new Error("Organization could not be created")).build();
    }
  }
}
