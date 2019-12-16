package io.tokern.bastion.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

  private static final ObjectMapper objectMapper = Jackson.newObjectMapper();

  private String jsonBody = "\"name\":\"DatabaseTest\"," +
      "\"jdbcUrl\":\"jdbc://localhost/bastion\"," +
      "\"userName\":\"user\"," +
      "\"password\":\"password\"," +
      "\"type\":\"MYSQL\"," +
      "\"orgId\":1";

  @Test
  void serializeToJson() throws Exception {
    final Database database = new Database("DatabaseTest", "jdbc://localhost/bastion",
        "user", "password", "MYSQL", 1);
    String serialized = objectMapper.writeValueAsString(database);

    String expected = "{\"id\":0," + jsonBody + "}";

    assertEquals(expected, serialized);
  }

  @Test
  void deserializeFromJson() throws Exception {
    String json = "{" + jsonBody + "}";
    Database database = objectMapper.readValue(json, Database.class);

    assertEquals(0, database.id);
    assertEquals("jdbc://localhost/bastion", database.jdbcUrl);
    assertEquals("user", database.userName);
    assertEquals("password", database.password);
    assertEquals(Database.Driver.MYSQL, database.type);
  }

  @Test
  void serializeDriver() throws Exception {
    Database.Driver driver = Database.Driver.valueOf("POSTGRESQL");
    String serialized = objectMapper.writeValueAsString(driver);

    assertEquals("{\"name\":\"Postgresql\",\"fields\":[{\"label\":\"JdbcUrl\",\"formType\":\"TEXT\"," +
        "\"key\":\"jdbcUrl\"},{\"label\":\"UserName\",\"formType\":\"TEXT\",\"key\":\"userName\"}," +
        "{\"label\":\"Password\",\"formType\":\"PASSWORD\",\"key\":\"password\"}],\"id\":\"POSTGRESQL\"}", serialized);
  }
}