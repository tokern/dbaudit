package io.tokern.bastion.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegisterTest {
  private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

  private String body = "\"orgName\":\"Tokern\",\"orgSlug\":\"https://tokern.io\"," +
      "\"displayName\":\"user0\"," +
      "\"userEmail\":\"user0@email.com\"," +
      "\"password\":\"passw0rd\"";

  @Test
  void serializeToJson() throws Exception {
    Register register = new Register("Tokern", "https://tokern.io",
        "user0", "user0@email.com", "passw0rd");
    String serialized = MAPPER.writeValueAsString(register);

    assertEquals("{" + body + "}", serialized);
  }

  @Test
  void deserializeFromJson() throws Exception {
    Register register = MAPPER.readValue("{" + body + "}", Register.class);

    assertEquals("Tokern", register.orgName);
  }


}
