package io.tokern.bastion.db;

import io.tokern.bastion.api.Database;
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
    assertEquals(3, databaseList.size());
  }

  @Test
  public void selectByIdurl() {
    Database database = databaseDAO.getByUrl("jdbc://localhost/bastion_1", 1);
    assertEquals("jdbc://localhost/bastion_1", database.jdbcUrl);

    Database byId = databaseDAO.getById(database.id, 1);
    assertNotNull(byId);
  }

  @Test
  public void update() {
    Database database = databaseDAO.getByUrl("jdbc://localhost/bastion_1", 1);

    Database updated = new Database(database.id,
        database.jdbcUrl,
        "userNew",
        database.password,
        database.type);

    databaseDAO.update(updated);

    Database databaseNew = databaseDAO.getById(database.id, 1);
    assertEquals("userNew", databaseNew.userName);
  }

  @Test
  public void delete() {
    Database database = databaseDAO.getByUrl("jdbc://localhost/bastion_3", 1);

    databaseDAO.deleteById(database.id, 1);

    Database databaseNew = databaseDAO.getById(database.id, 1);
    assertNull(databaseNew);
  }

  @Test
  public void createDatabase() {
    Long id = databaseDAO.insert(new Database("jdbc://localhost/bastion", "user", "password", "mysql"));

    List<Map<String,Object>> rows = handle.select("select * from dbs where id=?", id)
        .mapToMap().list();

    assertFalse(rows.isEmpty());

    Map<String, Object> row = rows.get(0);
    assertEquals(id.intValue(), row.get("id"));
    assertEquals("jdbc://localhost/bastion", row.get("jdbc_url"));
    assertEquals("user", row.get("user_name"));
    assertEquals("password", row.get("password"));
    assertEquals("mysql", row.get("type"));
  }
}