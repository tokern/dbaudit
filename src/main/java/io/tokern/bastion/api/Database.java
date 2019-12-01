package io.tokern.bastion.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Database {
  public final long id;
  public final String jdbcUrl;
  public final String userName;
  public final String password;
  public final String type;

  @JsonCreator
  public Database(@JsonProperty("id") long id,
                  @JsonProperty("jdbcUrl") String jdbcUrl,
                  @JsonProperty("userName") String userName,
                  @JsonProperty("password") String password,
                  @JsonProperty("type") String type) {
    this.id = id;
    this.jdbcUrl = jdbcUrl;
    this.userName = userName;
    this.password = password;
    this.type = type;
  }

  public Database(String jdbcUrl, String userName, String password, String type) {
    this.id = 0;
    this.jdbcUrl = jdbcUrl;
    this.userName = userName;
    this.password = password;
    this.type = type;
  }
}
