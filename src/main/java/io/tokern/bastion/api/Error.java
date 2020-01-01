package io.tokern.bastion.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Error {
  public final String error;

  @JsonCreator
  public Error(@JsonProperty("error") String error) {
    this.error = error;
  }
}
