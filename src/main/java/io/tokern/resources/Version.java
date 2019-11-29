package io.tokern.resources;

import com.codahale.metrics.annotation.Metered;
import io.tokern.api.GitState;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class Version {
  final GitState gitState;

  public Version(GitState gitState) {
    this.gitState = gitState;
  }

  @GET
  @Path("/version")
  @Metered
  public GitState version() {
    return gitState;
  }
}
