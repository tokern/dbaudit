package io.tokern.bastion.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

public class User {
  public final int id;
  public final String name;
  public final String email;
  public final byte[] passwordHash;
  public final String apiKey;
  public final int orgId;

  @JsonCreator
  @JdbiConstructor
  public User(
      @JsonProperty("id") @ColumnName("id") int id,
      @JsonProperty("name") @ColumnName("name") String name,
      @JsonProperty("email") @ColumnName("email") String email,
      @JsonProperty("passwordHash") @ColumnName("password_hash") byte[] passwordHash,
      @JsonProperty("apiKey") @ColumnName("api_key") String apiKey,
      @JsonProperty("orgId") @ColumnName("org_id") int orgId) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.passwordHash = passwordHash;
    this.apiKey = apiKey;
    this.orgId = orgId;
  }

  public User(String name, String email, byte[] passwordHash, String apiKey, int orgId) {
    this(0, name, email, passwordHash, apiKey, orgId);
  }

  public User(String name, String email, byte[] passwordHash, int orgId) {
    this(0, name, email, passwordHash, null, orgId);
  }
}
