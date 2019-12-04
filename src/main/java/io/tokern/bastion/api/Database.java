package io.tokern.bastion.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

public class Database {
  public final long id;
  public final String jdbcUrl;
  public final String userName;
  public final String password;
  public final String type;
  public final int orgId;

  @JsonCreator
  @JdbiConstructor
  public Database(@JsonProperty("id") @ColumnName("id") long id,
                  @JsonProperty("jdbcUrl") @ColumnName("jdbc_url") String jdbcUrl,
                  @JsonProperty("userName") @ColumnName("user_name") String userName,
                  @JsonProperty("password") @ColumnName("password") String password,
                  @JsonProperty("type") @ColumnName("type") String type,
                  @JsonProperty("orgId") @ColumnName("org_id") int orgId) {
    this.id = id;
    this.jdbcUrl = jdbcUrl;
    this.userName = userName;
    this.password = password;
    this.type = type;
    this.orgId = orgId;
  }

  public Database(String jdbcUrl, String userName, String password, String type, int orgId) {
    this(0, jdbcUrl, userName, password, type, orgId);
  }

  public static class UpdateRequest {
    private String jdbcUrl;
    private String userName;
    private String password;
    private String type;

    @JsonProperty
    public String getJdbcUrl() {
      return jdbcUrl;
    }

    @JsonProperty
    public void setJdbcUrl(String jdbcUrl) {
      this.jdbcUrl = jdbcUrl;
    }

    @JsonProperty
    public String getUserName() {
      return userName;
    }

    @JsonProperty
    public void setUserName(String userName) {
      this.userName = userName;
    }

    @JsonProperty
    public String getPassword() {
      return password;
    }

    @JsonProperty
    public void setPassword(String password) {
      this.password = password;
    }

    @JsonProperty
    public String getType() {
      return type;
    }

    @JsonProperty
    public void setType(String type) {
      this.type = type;
    }
  }
}
