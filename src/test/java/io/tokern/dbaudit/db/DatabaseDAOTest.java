package io.tokern.dbaudit.db;

import io.tokern.dbaudit.api.Database;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DatabaseDAOTest {
  private static Jdbi jdbi;
  private static Handle handle;
  private static DatabaseDAO databaseDAO;
  private static Flyway flyway;

  private static String url = "jdbc:postgresql://localhost/bastiondb";
  private static String user = "bastion";
  private static String password = "passw0rd";
  private static String schema = "bastion_schema";

  @BeforeAll
  public static void registerDriver() throws ClassNotFoundException {
    flyway = Flyway.configure()
        .dataSource(url, user, password)
        .schemas(schema)
        .defaultSchema(schema)
        .locations("db/migration", "fixtures/flywayMigrations").load();
    flyway.migrate();

    Class.forName("org.postgresql.Driver");

    jdbi = Jdbi.create(url + "?currentSchema=" + schema, user, password);
    jdbi.installPlugin(new SqlObjectPlugin());
    handle = jdbi.open();
    handle.registerRowMapper(ConstructorMapper.factory(Database.class));

    databaseDAO = handle.attach(DatabaseDAO.class);

 }

  @AfterAll
  public static void closeConnection() {
    flyway.clean();
    if (handle != null) {
      handle.close();
    }
  }

  @Test
  public void selectAll() {
    List<Database> databaseList = databaseDAO.listByOrgId(1);
    assertEquals(2, databaseList.size());
  }

  @Test
  public void selectByIdurl() {
    Database database = databaseDAO.getByUrl("jdbc://localhost/bastion_1", 1);
    assertEquals("jdbc://localhost/bastion_1", database.getJdbcUrl());

    Database byId = databaseDAO.getById(database.getId(), 1);
    assertNotNull(byId);
  }

  @Test
  public void selectByName() {
    Database database = databaseDAO.getByName("bastion_1", 1);
    assertEquals("jdbc://localhost/bastion_1", database.getJdbcUrl());
  }

  @Test
  public void update() {
    Database database = databaseDAO.getByUrl("jdbc://localhost/bastion_1", 1);

    Database updated = new Database(database.getId(),
        database.getName(),
        database.getJdbcUrl(),
        "userNew",
        database.getPassword(),
        database.getDriver(),
        database.getOrgId());

    databaseDAO.update(updated);

    Database databaseNew = databaseDAO.getById(database.getId(), 1);
    assertEquals("userNew", databaseNew.getUserName());
  }

  @Test
  public void delete() {
    Database database = databaseDAO.getByUrl("jdbc://localhost/bastion_3", 1);

    databaseDAO.deleteById(database.getId(), 1);

    Database databaseNew = databaseDAO.getById(database.getId(), 1);
    assertNull(databaseNew);
  }

  @Test
  public void createDatabase() {
    Long id = databaseDAO.insert(new Database("createDatabaseTest","jdbc://localhost/bastion",
        "user", "password", "MYSQL", 1));

    List<Map<String,Object>> rows = handle.select("select * from dbs where id=?", id)
        .mapToMap().list();

    assertFalse(rows.isEmpty());

    Map<String, Object> row = rows.get(0);
    assertEquals(id.intValue(), row.get("id"));
    assertEquals("createDatabaseTest", row.get("name"));
    assertEquals("jdbc://localhost/bastion", row.get("jdbc_url"));
    assertEquals("user", row.get("user_name"));
    assertEquals("password", row.get("password"));
    assertEquals("MYSQL", row.get("type"));
    assertEquals(1, row.get("org_id"));
  }
}