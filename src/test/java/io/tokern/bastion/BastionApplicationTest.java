package io.tokern.bastion;

import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class BastionApplicationTest {
  public static final DropwizardAppExtension<BastionConfiguration> EXTENSION =
      new DropwizardAppExtension<>(BastionApplication.class, resourceFilePath("test-config.yaml"));

  @Test
  public void canPerformAdminTaskWithPostBody() {
    final String response
        = EXTENSION.client().target("http://localhost:"
        + EXTENSION.getAdminPort() + "/ping")
        .request()
        .get(String.class);

    assertEquals("pong", response.strip());
  }
}