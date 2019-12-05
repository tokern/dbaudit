package io.tokern.bastion.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryTest {
  private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

  private static String body = "{\"sql\":\"select...\",\"userId\":1,\"dbId\":100,\"orgId\":1," +
      "\"state\":\"RUNNING\",\"id\":0}";

  @Test
  void serializeToJson() throws JsonProcessingException {
    Query query = new Query("select...", 1, 100,1, "RUNNING");

    String serialized = MAPPER.writeValueAsString(query);
    assertEquals(body, serialized);
  }

  @Test
  void deserializeFromJson() throws Exception {
    Query query = MAPPER.readValue(body, Query.class);

    assertEquals("select...", query.sql);
    assertEquals(1, query.userId);
    assertEquals(100, query.dbId);
    assertEquals(1, query.orgId);
    assertEquals(Query.State.RUNNING, query.state);
  }
}
