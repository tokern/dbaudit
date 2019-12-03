package io.tokern.bastion.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Register {
  public final String orgName;
  public final String orgSlug;
  public final String userName;
  public final String email;
  public final String password;

  @JsonCreator
  public Register(@JsonProperty("orgName") String orgName,
                  @JsonProperty("orgSlug") String orgSlug,
                  @JsonProperty("userName") String userName,
                  @JsonProperty("userEmail") String email,
                  @JsonProperty("password") String password) {

    this.orgName = orgName;
    this.orgSlug = orgSlug;
    this.userName = userName;
    this.email = email;
    this.password = password;
  }
}
