package io.tokern.bastion.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;
import org.mariadb.jdbc.MariaDbDataSource;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class Database {
  public enum Types {
    POSTGRESQL("org.postgresql.Driver", "org.postgresql.ds.PGSimpleDataSource"),
    MYSQL("org.postgresql.Driver", "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"),
    H2("org.h2.Driver", "org.h2.jdbcx.JdbcDataSource"),
    MARIADB("org.mariadb.jdbc.Driver", "org.mariadb.jdbc.MariaDbDataSource");

    public final String driverClass;
    public final String dataSourceClass;

    Types(String driverClass, String dataSourceClass) {
      this.driverClass = driverClass;
      this.dataSourceClass = dataSourceClass;
    }
  }

  public final long id;
  public final String jdbcUrl;
  public final String userName;
  public final String password;
  public final Types type;
  public final int orgId;

  public Database(long id, String jdbcUrl, String userName, String password, Types type, int orgId) {
    this.id = id;
    this.jdbcUrl = jdbcUrl;
    this.userName = userName;
    this.password = password;
    this.type = type;
    this.orgId = orgId;
  }

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
    this.type = Types.valueOf(type);
    this.orgId = orgId;
  }

  public Database(String jdbcUrl, String userName, String password, String type, int orgId) {
    this(0, jdbcUrl, userName, password, Types.valueOf(type), orgId);
  }

  @JsonIgnore
  public DataSource getDataSource() throws IllegalArgumentException, SQLException {
    if (type == Types.H2) {
      JdbcDataSource dataSource = new JdbcDataSource();
      dataSource.setUrl(this.jdbcUrl);
      dataSource.setUser(this.userName);
      dataSource.setPassword(this.password);
      return dataSource;
    } else if (type == Types.POSTGRESQL) {
      PGSimpleDataSource dataSource = new PGSimpleDataSource();
      dataSource.setUrl(this.jdbcUrl);
      dataSource.setUser(this.userName);
      dataSource.setPassword(this.password);
      return dataSource;
    } else if (type == Types.MYSQL) {
      MysqlDataSource dataSource = new MysqlDataSource();
      dataSource.setUrl(this.jdbcUrl);
      dataSource.setUser(this.userName);
      dataSource.setPassword(this.password);
      return dataSource;
    } else if (type == Types.MARIADB) {
      MariaDbDataSource dataSource = new MariaDbDataSource();
      dataSource.setUrl(this.jdbcUrl);
      dataSource.setUser(this.userName);
      dataSource.setPassword(this.password);
      return dataSource;
    }

    throw new IllegalArgumentException(String.format("%s database is not supported", type));
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
