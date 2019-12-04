package io.tokern.bastion.db;

import io.tokern.bastion.api.Organization;
import io.tokern.bastion.api.Register;
import io.tokern.bastion.api.User;
import io.tokern.bastion.core.auth.PasswordDigest;
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

import static org.junit.jupiter.api.Assertions.*;

public class RegisterDAOTest {
  private static Jdbi jdbi;
  private static Handle handle;
  private static Flyway flyway;

  private static OrganizationDAO organizationDAO;
  private static UserDAO userDAO;

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
        .locations("db/migration").load();
    flyway.migrate();

    Class.forName("org.postgresql.Driver");

    jdbi = Jdbi.create(url + "?currentSchema=" + schema, user, password);
    jdbi.installPlugin(new SqlObjectPlugin());
    handle = jdbi.open();
    handle.registerRowMapper(ConstructorMapper.factory(Organization.class));
    handle.registerRowMapper(ConstructorMapper.factory(User.class));

    organizationDAO = handle.attach(OrganizationDAO.class);
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
  void testInsert() {
    Register register = new Register("testOrg", "testSlug", "testUser", "testEmail", "testPwd");
    RegisterDAO.insert(jdbi, register);

    Organization organization = organizationDAO.getByName("testOrg");
    assertNotNull(organization);
    assertEquals("testSlug", organization.slug);

    List<User> users = userDAO.listByOrg(organization.id);
    assertEquals(1, users.size());

    User user = users.get(0);
    PasswordDigest digest = PasswordDigest.fromDigest(user.passwordHash);

    assertEquals("testUser", user.name);
    assertEquals("testEmail", user.email);
    assertTrue(digest.checkPassword("testPwd".getBytes(StandardCharsets.UTF_8)));
  }
}
