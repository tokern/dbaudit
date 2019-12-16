package io.tokern.bastion.api;

import com.fasterxml.jackson.annotation.*;
import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;
import org.mariadb.jdbc.MariaDbDataSource;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.List;

@JsonPropertyOrder({"id", "name", "jdbcUrl", "userName", "password", "driver", "orgId"})
public class Database {
  public enum FieldType {
    TEXT,
    PASSWORD
  };

  public static class Field {
    public final String label;
    public final FieldType formType;

    public Field(String label, FieldType fieldType) {
      this.label = label;
      this.formType = fieldType;
    }

    public String getKey() {
      return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, label);
    }
  }

  @JsonFormat(shape= JsonFormat.Shape.OBJECT)
  public enum Driver {
    POSTGRESQL("org.postgresql.Driver", "org.postgresql.ds.PGSimpleDataSource"),
    MYSQL("org.postgresql.Driver", "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"),
    H2("org.h2.Driver", "org.h2.jdbcx.JdbcDataSource"),
    MARIADB("org.mariadb.jdbc.Driver", "org.mariadb.jdbc.MariaDbDataSource");

    @JsonIgnore
    public final String driverClass;
    @JsonIgnore
    public final String dataSourceClass;

    private static List<Field> fieldList = ImmutableList.of(
        new Field("JdbcUrl", FieldType.TEXT),
        new Field("UserName", FieldType.TEXT),
        new Field("Password", FieldType.PASSWORD)
    );

    Driver(String driverClass, String dataSourceClass) {
      this.driverClass = driverClass;
      this.dataSourceClass = dataSourceClass;
    }

    @JsonProperty
    public String getId() {
      return this.name();
    }

    @JsonProperty
    public String getName() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, this.name());
    }

    @JsonProperty
    public List<Field> getFields() {
      return fieldList;
    }
  }

  final long id;
  @NotNull
  final String name;
  @NotNull
  final String jdbcUrl;
  @NotNull
  final String userName;
  @NotNull
  final String password;
  @NotNull
  final Driver driver;
  @NotNull
  final int orgId;

  public Database(long id, String name, String jdbcUrl, String userName, String password, Driver driver, int orgId) {
    this.id = id;
    this.name = name;
    this.jdbcUrl = jdbcUrl;
    this.userName = userName;
    this.password = password;
    this.driver = driver;
    this.orgId = orgId;
  }

  @JdbiConstructor
  public Database(@ColumnName("id") long id,
                  @ColumnName("name") String name,
                  @ColumnName("jdbc_url") String jdbcUrl,
                  @ColumnName("user_name") String userName,
                  @ColumnName("password") String password,
                  @ColumnName("driver") String driver,
                  @ColumnName("org_id") int orgId) {
    this(id, name, jdbcUrl, userName, password, Driver.valueOf(driver), orgId);
  }


  @JsonCreator
  public Database(@JsonProperty("name") String name,
                  @JsonProperty("jdbcUrl") String jdbcUrl,
                  @JsonProperty("userName") String userName,
                  @JsonProperty("password") String password,
                  @JsonProperty("driver") String driver) {
    this(0, name, jdbcUrl, userName, password, Driver.valueOf(driver), 0);
  }

  public Database(String name, String jdbcUrl, String userName, String password, String driver, int orgId) {
    this(0, name, jdbcUrl, userName, password, Driver.valueOf(driver), orgId);
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getJdbcUrl() {
    return jdbcUrl;
  }

  public String getUserName() {
    return userName;
  }

  public String getPassword() {
    return password;
  }

  public String getDriver() {
    return driver.name();
  }

  public int getOrgId() {
    return orgId;
  }

  @JsonIgnore
  public Driver getDriverType() {
    return driver;
  }

  @JsonIgnore
  public DataSource getDataSource() throws IllegalArgumentException, SQLException {
    if (driver == Driver.H2) {
      JdbcDataSource dataSource = new JdbcDataSource();
      dataSource.setUrl(this.jdbcUrl);
      dataSource.setUser(this.userName);
      dataSource.setPassword(this.password);
      return dataSource;
    } else if (driver == Driver.POSTGRESQL) {
      PGSimpleDataSource dataSource = new PGSimpleDataSource();
      dataSource.setUrl(this.jdbcUrl);
      dataSource.setUser(this.userName);
      dataSource.setPassword(this.password);
      return dataSource;
    } else if (driver == Driver.MYSQL) {
      MysqlDataSource dataSource = new MysqlDataSource();
      dataSource.setUrl(this.jdbcUrl);
      dataSource.setUser(this.userName);
      dataSource.setPassword(this.password);
      return dataSource;
    } else if (driver == Driver.MARIADB) {
      MariaDbDataSource dataSource = new MariaDbDataSource();
      dataSource.setUrl(this.jdbcUrl);
      dataSource.setUser(this.userName);
      dataSource.setPassword(this.password);
      return dataSource;
    }

    throw new IllegalArgumentException(String.format("%s database is not supported", driver));
  }

  public static class DatabaseList {
    public final List<Database> databases;

    public DatabaseList(List<Database> databases) {
      this.databases = databases;
    }
  }

  public static class DriverList {
    public final List<Driver> drivers;

    public DriverList(List<Driver> drivers) {
      this.drivers = drivers;
    }
  }

  public static class UpdateRequest {
    private String name;
    private String jdbcUrl;
    private String userName;
    private String password;
    private String type;

    @JsonProperty
    public String getName() {
      return name;
    }

    @JsonProperty
    public void setName(String name) {
      this.name = name;
    }

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
