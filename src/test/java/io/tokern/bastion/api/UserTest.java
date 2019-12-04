package io.tokern.bastion.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
  private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

  private String requestBody = "{\"name\":\"requestName\",\"email\":\"requestEmail\"," +
      "\"password\":\"requestPassword\",\"systemRole\":\"requestRole\"}";

  @Test
  void serializeRequestToJson() throws JsonProcessingException {
    User.Request request = new User.Request("requestName", "requestEmail",
        "requestPassword", "requestRole");

    String serialized = MAPPER.writeValueAsString(request);

    assertEquals(requestBody, serialized);
  }

  @Test
  void deserializeRequestFromJson() throws Exception {
    User.Request request = MAPPER.readValue(requestBody, User.Request.class);

    assertEquals("requestName", request.name);
    assertEquals("requestEmail", request.email);
    assertEquals("requestPassword", request.password);
    assertEquals("requestRole", request.systemRole);
  }

  private String changePasswordBody = "{\"currentPassword\":\"current\",\"newPassword\":\"new\"}";

  @Test
  void serializeChangePasswordToJson() throws JsonProcessingException {
    User.PasswordChange change = new User.PasswordChange("current", "new");

    String serialized = MAPPER.writeValueAsString(change);

    assertEquals(changePasswordBody, serialized);
  }

  @Test
  void deserializeChangePasswordFromJson() throws Exception {
    User.PasswordChange change = MAPPER.readValue(changePasswordBody, User.PasswordChange.class);

    assertEquals("current", change.currentPassword);
    assertEquals("new", change.newPassword);
  }
}
