package io.tokern.dbaudit.db;

import io.tokern.dbaudit.api.Query;
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

import static org.junit.jupiter.api.Assertions.*;

class QueryDAOTest {
  private static Jdbi jdbi;
  private static Handle handle;
  private static QueryDAO queryDAO;
  private static Flyway flyway;

  private static String url = "jdbc:postgresql://localhost/bastiondb";
  private static String user = "bastion";
  private static String password = "passw0rd";
  private static String schema = "bastion_schema";

  @BeforeAll
  static void registerDriver() throws ClassNotFoundException {
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
    handle.registerRowMapper(ConstructorMapper.factory(Query.class));

    queryDAO = handle.attach(QueryDAO.class);
 }

  @AfterAll
  static void closeConnection() {
    flyway.clean();
    if (handle != null) {
      handle.close();
    }
  }

  @Test
  void selectAll() {
    List<Query> userList = queryDAO.listByOrg(1);
    assertEquals(2, userList.size());
  }

  @Test
  void selectByUser() {
    List<Query> userList = queryDAO.listByUser(3, 1);
    assertEquals(1, userList.size());
  }

  @Test
  void selectById() {
    Query query = queryDAO.getById(1, 1);
    assertEquals("select 1", query.sql);
  }

  @Test
  void updateState() {
    assertEquals(Query.State.RUNNING, queryDAO.getById(1, 1).state);

    queryDAO.updateState(1, 1, Query.State.ERROR);

    assertEquals(Query.State.ERROR, queryDAO.getById(1, 1).state);
  }

  @Test
  void delete() {
    queryDAO.deleteById(3, 2);
    assertNull(queryDAO.getById(3, 2));
  }

  @Test
  void create() {
    Long id = queryDAO.insert(new Query("select 4", 2, 1, 1, "WAITING"));

    List<Map<String,Object>> rows = handle.select("select * from queries where id=?", id)
        .mapToMap().list();
    assertFalse(rows.isEmpty());

    Map<String, Object> row = rows.get(0);
    assertEquals(id.intValue(), row.get("id"));
    assertEquals("select 4", row.get("sql"));
    assertEquals(2, row.get("user_id"));
    assertEquals(1, row.get("db_id"));
    assertEquals(1, row.get("org_id"));
    assertEquals("WAITING", row.get("state"));
  }
}
