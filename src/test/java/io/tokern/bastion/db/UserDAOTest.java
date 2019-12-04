package io.tokern.bastion.db;

import io.tokern.bastion.api.User;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static io.tokern.bastion.api.User.SystemRoles.USER;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
  private static Jdbi jdbi;
  private static Handle handle;
  private static UserDAO userDAO;
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
    handle.registerRowMapper(ConstructorMapper.factory(User.class));

    userDAO = handle.attach(UserDAO.class);

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
    List<User> userList = userDAO.list();
    assertEquals(4, userList.size());
  }

  @Test
  public void selectById() {
    User user = userDAO.getById(1);
    assertEquals("tokern_admin", user.name);
  }

  @Test
  public void update() {
    User user = userDAO.getById(1);
    assertEquals("admin@tokern", user.email);

    User updated = new User(user.id,
        user.name,
        "admin2@tokern",
        user.passwordHash,
        user.systemRole.name(),
        user.orgId);

    userDAO.update(updated);

    User userNew = userDAO.getById(1);
    assertEquals("admin2@tokern", userNew.email);
  }

  @Test
  public void delete() {
    User user = userDAO.getById(2);

    userDAO.deleteById(user.id);

    assertNull(userDAO.getById(2));
  }

  @Test
  public void createUser() {
    Long id = userDAO.insert(new User("tokern_sysops", "sysops@tokern",
        "SYSYSY".getBytes(StandardCharsets.UTF_8), USER, 1));

    List<Map<String,Object>> rows = handle.select("select * from users where id=?", id)
        .mapToMap().list();

    assertFalse(rows.isEmpty());

    Map<String, Object> row = rows.get(0);
    assertEquals(id.intValue(), row.get("id"));
    assertEquals("tokern_sysops", row.get("name"));
    assertEquals("sysops@tokern", row.get("email"));
    assertNotNull(row.get("password_hash"));
    assertEquals("USER", row.get("system_role"));
    assertEquals(1, row.get("org_id"));
  }
}
