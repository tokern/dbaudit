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

  @JsonCreator
  @JdbiConstructor
  public Database(@JsonProperty("id") @ColumnName("id") long id,
                  @JsonProperty("jdbcUrl") @ColumnName("jdbc_url") String jdbcUrl,
                  @JsonProperty("userName") @ColumnName("user_name") String userName,
                  @JsonProperty("password") @ColumnName("password") String password,
                  @JsonProperty("type") @ColumnName("type") String type) {
    this.id = id;
    this.jdbcUrl = jdbcUrl;
    this.userName = userName;
    this.password = password;
    this.type = type;
  }

  public Database(String jdbcUrl, String userName, String password, String type) {
    this(0, jdbcUrl, userName, password, type);
  }
}
