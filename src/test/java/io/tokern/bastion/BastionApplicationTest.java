package io.tokern.bastion;

import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.tokern.bastion.api.*;
import io.tokern.bastion.core.auth.JwtTokenManager;
import io.tokern.bastion.core.auth.PasswordDigest;
import io.tokern.bastion.db.OrganizationDAO;
import io.tokern.bastion.db.UserDAO;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.stream.Stream;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class BastionApplicationTest {
  public static final DropwizardAppExtension<BastionConfiguration> EXTENSION =
      new DropwizardAppExtension<>(BastionApplication.class, resourceFilePath("test-config.yaml"));

  static Flyway flyway;

  static String adminToken;
  static String dbAdminToken;
  static String userToken;

  static User loggedInAdmin;
  static User loggedInDbAdmin;
  static User loggedInUser;

  @BeforeAll
  static void setupDatabase() throws ClassNotFoundException {
    ManagedDataSource dataSource = EXTENSION.getConfiguration().getDataSourceFactory()
        .build(EXTENSION.getEnvironment().metrics(), "flyway");
    flyway = EXTENSION.getConfiguration().getFlywayFactory().build(dataSource);
    flyway.migrate();

    Class.forName("org.postgresql.Driver");

    Jdbi jdbi = Jdbi.create(dataSource);
    jdbi.installPlugin(new SqlObjectPlugin());
    Long orgId = jdbi.withExtension(OrganizationDAO.class, dao -> dao.insert(new Organization("Tokern", "http://tokern.io")));
    jdbi.useExtension(UserDAO.class, dao -> dao.insert(new User(
        "tokern_root", "root@tokern.io",
        PasswordDigest.generateFromPassword("passw0rd").getDigest(),
        User.SystemRoles.ADMIN,
        orgId.intValue()
    )));

    jdbi.useExtension(UserDAO.class, dao -> dao.insert(new User(
        "tokern_db", "db@tokern.io",
        PasswordDigest.generateFromPassword("passw0rd").getDigest(),
        User.SystemRoles.DBADMIN,
        orgId.intValue()
    )));

    jdbi.useExtension(UserDAO.class, dao -> dao.insert(new User(
        "tokern_user", "user@tokern.io",
        PasswordDigest.generateFromPassword("passw0rd").getDigest(),
        User.SystemRoles.USER,
        orgId.intValue()
    )));

    loggedInAdmin = jdbi.withExtension(UserDAO.class, dao -> dao.getByEmail("root@tokern.io"));
    loggedInDbAdmin = jdbi.withExtension(UserDAO.class, dao -> dao.getByEmail("db@tokern.io"));
    loggedInUser = jdbi.withExtension(UserDAO.class, dao -> dao.getByEmail("user@tokern.io"));

    JwtTokenManager tokenManager = new JwtTokenManager(EXTENSION.getConfiguration().getJwtConfiguration().getJwtSecret());

    adminToken = tokenManager.generateToken(loggedInAdmin);
    dbAdminToken = tokenManager.generateToken(loggedInDbAdmin);
    userToken = tokenManager.generateToken(loggedInUser);
  }

  @AfterAll
  static void tearDownDatabase() {
    flyway.clean();
  }

  @Test
  void canPerformAdminTaskWithPostBody() {
    final String response
        = EXTENSION.client().target("http://localhost:"
        + EXTENSION.getAdminPort() + "/ping")
        .request()
        .get(String.class);

    assertEquals("pong", response.strip());
  }

  @Test
  void registerTest() {
    Register register = new Register("RegisterTestOrg", "https://tokern.io",
        "userRoot", "root@test.org", "root_passw0rd");
    Entity<?> entity = Entity.entity(register, MediaType.APPLICATION_JSON);

    final Response response
        = EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/register")
        .request().post(entity);

    assertEquals(200, response.getStatus());
  }

  @Test
  void loginTest() {
    LoginRequest request = new LoginRequest("root@tokern.io", "passw0rd");
    Entity<?> entity = Entity.entity(request, MediaType.APPLICATION_JSON);

    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/users/login")
        .request().post(entity);
    LoginResponse loginResponse = response.readEntity(LoginResponse.class);

    assertEquals(200, response.getStatus());
    assertNotNull(loginResponse);
    assertFalse(loginResponse.token.isEmpty());
  }

  @Test
  void protectedResourceTest() {
    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/users/" + loggedInUser.id)
        .request().header("Authorization", "BEARER " + adminToken).get();

    assertEquals(200, response.getStatus());

    User user = response.readEntity(User.class);
    assertEquals(loggedInUser.id, user.id);
    assertEquals(loggedInUser.orgId, user.orgId);
    assertEquals(loggedInUser.email, user.email);
    assertEquals(loggedInUser.name, user.name);
    assertNull(user.passwordHash);
  }

  @Test
  void disallowRole() {
    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/users/" + loggedInUser.id)
            .request().header("Authorization", "BEARER " + userToken).get();

    assertEquals(403, response.getStatus());
  }

  private static Stream<Arguments> provideUsersAndTokens() {
    return Stream.of(
      Arguments.of(loggedInUser, userToken),
        Arguments.of(loggedInAdmin, adminToken),
        Arguments.of(loggedInDbAdmin, dbAdminToken)
    );
  }

  @ParameterizedTest
  @MethodSource("provideUsersAndTokens")
  void mePermitAll(User caller, String token) {
    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/users/me")
        .request().header("Authorization", "BEARER " + token).get();

    assertEquals(200, response.getStatus());
    User user = response.readEntity(User.class);
    assertEquals(caller.id, user.id);
    assertEquals(caller.orgId, user.orgId);
    assertEquals(caller.email, user.email);
    assertEquals(caller.name, user.name);
  }
}