package io.tokern.bastion;

import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.tokern.bastion.api.*;
import io.tokern.bastion.core.auth.JwtTokenManager;
import io.tokern.bastion.db.DatabaseDAO;
import io.tokern.bastion.db.UserDAO;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Stream;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(DropwizardExtensionsSupport.class)
class BastionApplicationTest {
  private static Logger logger = LoggerFactory.getLogger(BastionApplicationTest.class);

  static final DropwizardAppExtension<BastionConfiguration> EXTENSION =
      new DropwizardAppExtension<>(BastionApplication.class, resourceFilePath("test-config.yaml"))
      .addListener(new ApplicationDatabaseSetup());

  static Jdbi jdbi;

  static String adminToken;
  static String dbAdminToken;
  static String userToken;

  static User loggedInAdmin;
  static User loggedInDbAdmin;
  static User loggedInUser;
  static User updateUser;

  static List<Database> databases;
  @BeforeAll
  static void setupDatabase() throws ClassNotFoundException {
    ManagedDataSource dataSource = EXTENSION.getConfiguration().getDataSourceFactory()
        .build(EXTENSION.getEnvironment().metrics(), "flyway");

    Class.forName("org.postgresql.Driver");

    jdbi = Jdbi.create(dataSource);
    jdbi.installPlugin(new SqlObjectPlugin());

    loggedInAdmin = jdbi.withExtension(UserDAO.class, dao -> dao.getByEmail("root@tokern.io"));
    loggedInDbAdmin = jdbi.withExtension(UserDAO.class, dao -> dao.getByEmail("db@tokern.io"));
    loggedInUser = jdbi.withExtension(UserDAO.class, dao -> dao.getByEmail("user@tokern.io"));
    updateUser = jdbi.withExtension(UserDAO.class, dao -> dao.getByEmail("put@tokern.io"));

    JwtTokenManager tokenManager = new JwtTokenManager(EXTENSION.getConfiguration().getJwtConfiguration().getJwtSecret());

    adminToken = tokenManager.generateToken(loggedInAdmin);
    dbAdminToken = tokenManager.generateToken(loggedInDbAdmin);
    userToken = tokenManager.generateToken(loggedInUser);
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

  @Test
  void createUserTest() {
    User.Request request = new User.Request("tokern_devops", "devops@tokern.io", "opsw0rd", "USER");
    Entity<?> entity = Entity.entity(request, MediaType.APPLICATION_JSON);

    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/users/")
        .request().header("Authorization", "BEARER " + adminToken).post(entity);

    assertEquals(200, response.getStatus());
    User user = response.readEntity(User.class);

    assertEquals("devops@tokern.io", user.email);
  }

  @ParameterizedTest
  @MethodSource("provideUsersAndTokens")
  void refreshToken(User caller, String token) {
    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/users/refreshJWT")
        .request().header("Authorization", "BEARER " + token).get();

    String refreshed = response.readEntity(String.class);

    assertEquals(200, response.getStatus());
    assertNotNull(refreshed);
  }

  @Test
  void updateUserTest() {
    User.Request request = new User.Request(null, "putter@tokern.io", null, null);
    Entity<?> entity = Entity.entity(request, MediaType.APPLICATION_JSON);

    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/users/" + updateUser.id)
        .request().header("Authorization", "BEARER " + adminToken).put(entity);

    assertEquals(200, response.getStatus());
    User user = response.readEntity(User.class);

    assertEquals("putter@tokern.io", user.email);
  }

  @Test
  void changePasswordTest() {
    User.PasswordChange change = new User.PasswordChange("passw0rd", "changew0rd");
    Entity<?> entity = Entity.entity(change, MediaType.APPLICATION_JSON);

    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/users/changePassword")
            .request().header("Authorization", "BEARER " + userToken).put(entity);

    assertEquals(200, response.getStatus());
    User user = jdbi.withExtension(UserDAO.class, dao -> dao.getByEmail("user@tokern.io"));
    assertTrue(user.login("changew0rd"));
  }

  private static Stream<Arguments> provideAdminAndDbAdmin() {
    return Stream.of(
        Arguments.of(loggedInAdmin, adminToken),
        Arguments.of(loggedInDbAdmin, dbAdminToken)
    );
  }

  @Order(1)
  @ParameterizedTest
  @MethodSource("provideUsersAndTokens")
  void listDatabases(User user, String token) {
    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/databases/")
            .request().header("Authorization", "BEARER " + token).get();

    assertEquals(200, response.getStatus());
    List<Database> databases = response.readEntity(new GenericType<List<Database>>() {});

    assertEquals(2, databases.size());
  }

  @ParameterizedTest
  @MethodSource("provideUsersAndTokens")
  void getDatabase(User user, String token) {
    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/databases/1")
            .request().header("Authorization", "BEARER " + token).get();

    assertEquals(200, response.getStatus());
    Database database = response.readEntity(Database.class);

    assertEquals("jdbc:postgresql://localhost/bastiondb?currentSchema=bastion_app", database.jdbcUrl);
  }

  @ParameterizedTest
  @MethodSource("provideAdminAndDbAdmin")
  void createDatabase(User user, String token) {
    Database database = new Database("jdbc://localhost/bastion3", "user", "password", "MYSQL", user.orgId);
    Entity<?> entity = Entity.entity(database, MediaType.APPLICATION_JSON);

    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/databases/")
            .request().header("Authorization", "BEARER " + token).post(entity);

    assertEquals(200, response.getStatus());
    Database created = response.readEntity(Database.class);

    assertEquals("jdbc://localhost/bastion3", created.jdbcUrl);
  }

  @Test
  void deleteDatabase() {
    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/databases/2")
            .request().header("Authorization", "BEARER " + dbAdminToken).delete();

    assertEquals(200, response.getStatus());
    Database database = jdbi.withExtension(DatabaseDAO.class, dao -> dao.getById(2, loggedInDbAdmin.orgId));
    assertNull(database);
  }

  @Test
  void updateDatabase() {
    Database.UpdateRequest request = new Database.UpdateRequest();
    request.setUserName("tokern");
    Entity<?> entity = Entity.entity(request, MediaType.APPLICATION_JSON);

    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/databases/1")
            .request().header("Authorization", "BEARER " + dbAdminToken).put(entity);

    assertEquals(200, response.getStatus());
    Database updated = response.readEntity(Database.class);

    assertEquals("tokern", updated.userName);
  }

  @Test
  void listQueries() {
    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/queries/")
            .request().header("Authorization", "BEARER " + dbAdminToken).get();

    assertEquals(200, response.getStatus());
    List<Query> queries = response.readEntity(new GenericType<List<Query>>() {});

    // 1 extra because of runQuery
    assertEquals(2, queries.size());
  }

  @Test
  void getQuery() {
    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/queries/1")
            .request().header("Authorization", "BEARER " + userToken).get();

    assertEquals(200, response.getStatus());
    Query query = response.readEntity(Query.class);

    assertEquals("select 1", query.sql);
  }

  @ParameterizedTest
  @MethodSource("provideUsersAndTokens")
  void runQuery(User user, String token) throws InterruptedException {
    Query query = new Query("SELECT 1 as ONE", user.id, 1, user.orgId);
    Entity<?> entity = Entity.entity(query, MediaType.APPLICATION_JSON);

    final Response response =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/queries/")
            .request().header("Authorization", "BEARER " + token).post(entity);

    assertEquals(200, response.getStatus());
    Query created = response.readEntity(Query.class);

    assertTrue(created.id > 0);
    assertEquals(Query.State.RUNNING, created.state);

    while (created.state == Query.State.RUNNING || created.state == Query.State.WAITING) {
      Thread.sleep(500);
      final Response checkStatus =
          EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() +
              "/api/queries/" + created.id)
              .request().header("Authorization", "BEARER " + token).get();

      assertEquals(200, checkStatus.getStatus());
      created = checkStatus.readEntity(Query.class);
    }

    assertEquals(Query.State.SUCCESS, created.state);

    //Get query result
    final Response queryResult =
        EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() +
            "/api/queries/" + created.id + "/results")
            .request().header("Authorization", "BEARER " + token).get();


    assertEquals(200, queryResult.getStatus());
    String rowSet = queryResult.readEntity(String.class);

    assertEquals("[{\"one\":1}]", rowSet);
  }
}