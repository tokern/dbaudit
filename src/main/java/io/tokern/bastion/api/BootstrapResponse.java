package io.tokern.bastion.api;

import io.tokern.bastion.core.FEConfiguration;

public class BootstrapResponse {
  public static class Config {
    public final String publicUrl;
    public final boolean allowCsvDownload;
    public final boolean editorWordWrap;
    public final String baseUrl;
    public final boolean smtpConfigured;
    public final boolean googleAuthConfigured;
    public final boolean localAuthConfigured;
    public final boolean samlConfigured;

    public Config(FEConfiguration feConfiguration) {
      this.publicUrl = feConfiguration.getPublicUrl();
      this.allowCsvDownload = feConfiguration.isAllowCsvDownload();
      this.editorWordWrap = feConfiguration.isEditorWordWrap();
      this.baseUrl = feConfiguration.getBaseUrl();
      this.smtpConfigured = !feConfiguration.getSmtpFrom().isBlank();
      this.googleAuthConfigured = !feConfiguration.getGoogleClientId().isBlank();
      this.localAuthConfigured = !feConfiguration.isDisableUserPassAuth();
      this.samlConfigured = !feConfiguration.getSamlIssuer().isBlank();
    }
  }

  public final boolean adminRegistrationOpen;
  public final String currentUser;
  public final Config config;
  public final String version;

  public BootstrapResponse(boolean adminRegistrationOpen,
                           String currentUser,
                           Config config,
                           String version) {
    this.adminRegistrationOpen = adminRegistrationOpen;
    this.currentUser = currentUser;
    this.config = config;
    this.version = version;
  }
}
