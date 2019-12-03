package io.tokern.bastion.resources;

import com.codahale.metrics.annotation.Timed;
import io.tokern.bastion.api.Register;
import io.tokern.bastion.db.RegisterDAO;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/register")
public class RegisterResource {
  private final Jdbi jdbi;

  public RegisterResource(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @POST
  @Timed
  public Response newRegistration(Register register) {
    RegisterDAO.insert(jdbi, register);
    return Response.ok("Organization " + register.orgName + " with root user " + register.userName + " created")
        .build();
  }
}
