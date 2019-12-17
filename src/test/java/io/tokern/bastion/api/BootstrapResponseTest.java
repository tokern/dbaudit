package io.tokern.bastion.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import io.tokern.bastion.core.FEConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BootstrapResponseTest {
  private static final ObjectMapper objectMapper = Jackson.newObjectMapper();
  @Test
  void serializeToJson() throws Exception {
    BootstrapResponse response = new BootstrapResponse(false, "user",
        new BootstrapResponse.Config(new FEConfiguration()), "0.1");
    String serialized = objectMapper.writeValueAsString(response);
    assertEquals("{\"adminRegistrationOpen\":false,\"currentUser\":\"user\"," +
        "\"config\":{\"publicUrl\":\"\",\"allowCsvDownload\":true,\"editorWordWrap\":false,\"baseUrl\":\"\"," +
        "\"smtpConfigured\":false,\"googleAuthConfigured\":false,\"localAuthConfigured\":true," +
        "\"samlConfigured\":false},\"version\":\"0.1\"}", serialized);
  }

}