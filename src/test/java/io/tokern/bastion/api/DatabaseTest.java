package io.tokern.bastion.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

  private static final ObjectMapper objectMapper = Jackson.newObjectMapper();

  private String jsonBody = "\"jdbcUrl\":\"jdbc://localhost/bastion\"," +
          "\"userName\":\"user\"," +
          "\"password\":\"password\"," +
          "\"type\":\"mysql\"," +
          "\"orgId\":1";

  @Test
  void serializeToJson() throws Exception {
    final Database database = new Database("jdbc://localhost/bastion", "user", "password", "mysql", 1);
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
    assertEquals("mysql", database.type);
  }
}