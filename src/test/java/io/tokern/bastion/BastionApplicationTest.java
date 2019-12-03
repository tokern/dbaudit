package io.tokern.bastion;

import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.tokern.bastion.api.Register;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class BastionApplicationTest {
  public static final DropwizardAppExtension<BastionConfiguration> EXTENSION =
      new DropwizardAppExtension<>(BastionApplication.class, resourceFilePath("test-config.yaml"));

  static Flyway flyway;

  @BeforeAll
  static void setupDatabase() {
    ManagedDataSource dataSource = EXTENSION.getConfiguration().getDataSourceFactory()
        .build(EXTENSION.getEnvironment().metrics(), "flyway");
    flyway = EXTENSION.getConfiguration().getFlywayFactory().build(dataSource);
    flyway.migrate();
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
    Register register = new Register("Tokern", "https://tokern.io",
        "tokernRoot", "root@tokern", "passw0rd");
    Entity<?> entity = Entity.entity(register, MediaType.APPLICATION_JSON);


    final Response response
        = EXTENSION.client().target("http://localhost:" + EXTENSION.getLocalPort() + "/api/register")
        .request().post(entity);

    assertEquals(200, response.getStatus());
  }
}