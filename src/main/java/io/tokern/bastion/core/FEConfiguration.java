package io.tokern.bastion.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class FEConfiguration extends Configuration {
  String cookieName = "sqlpad.sid";
  String cookieSecret = "secret-used-to-sign-cookies-please-set-and-make-strong";
  int sessionMinutes = 60;
  int timeoutSeconds = 300;
  String serverIp = "0.0.0.0";
  int serverPort = 80;
  boolean systemdSocket = false;
  int httpsPort = 443;
  String dbPath = "";
  String baseUrl = "";
  String passphrase = "At least the sensitive bits won't be plain text?";
  String certPassphrase = "";
  String keyPath = "";
  String certPath = "";
  String admin = "";
  String adminPassword = "";
  boolean debug = false;
  String googleClientId = "";
  String googleClientSecret = "";
  String publicUrl = "";
  boolean disableUserPassAuth = false;
  boolean allowCsvDownload = true;
  boolean editorWordWrap = false;
  int queryResultMaxRows = 50000;
  String slackWebhook = "";
  boolean tableChartLinksRequireAuth = true;
  String smtpFrom = "";
  String smtpHost = "";
  String smtpPort = "";
  boolean smtpSecure = true;
  String smtpUser = "";
  String smtpPassword = "";
  String whitelistedDomains = "";
  String samlEntryPoint = "";
  String samlIssuer = "";
  String samlCallbackUrl = "";
  String samlCert = "";
  String samlAuthContext = "";

  @JsonProperty("cookieName")
  public String getCookieName() {
    return cookieName;
  }

  @JsonProperty("cookieName")
  public void setCookieName(String cookieName) {
    this.cookieName = cookieName;
  }

  @JsonProperty("cookieSecret")
  public String getCookieSecret() {
    return cookieSecret;
  }

  @JsonProperty("cookieSecret")
  public void setCookieSecret(String cookieSecret) {
    this.cookieSecret = cookieSecret;
  }

  @JsonProperty("sessionMinutes")
  public int getSessionMinutes() {
    return sessionMinutes;
  }
  @JsonProperty("sessionMinutes")
  public void setSessionMinutes(int sessionMinutes) {
    this.sessionMinutes = sessionMinutes;
  }

  @JsonProperty("timeOutSeconds")
  public int getTimeoutSeconds() {
    return timeoutSeconds;
  }

  @JsonProperty("timeOutSeconds")
  public void setTimeoutSeconds(int timeoutSeconds) {
    this.timeoutSeconds = timeoutSeconds;
  }

  @JsonProperty("serverIp")
  public String getServerIp() {
    return serverIp;
  }

  @JsonProperty("serverIp")
  public void setServerIp(String serverIp) {
    this.serverIp = serverIp;
  }

  @JsonProperty("serverPort")
  public int getServerPort() {
    return serverPort;
  }

  @JsonProperty("serverPort")
  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }

  @JsonProperty("systemdSocket")
  public boolean isSystemdSocket() {
    return systemdSocket;
  }

  @JsonProperty("systemdSocket")
  public void setSystemdSocket(boolean systemdSocket) {
    this.systemdSocket = systemdSocket;
  }

  @JsonProperty("httpsPort")
  public int getHttpsPort() {
    return httpsPort;
  }

  @JsonProperty("httpsPort")
  public void setHttpsPort(int httpsPort) {
    this.httpsPort = httpsPort;
  }

  @JsonProperty("dbPath")
  public String getDbPath() {
    return dbPath;
  }

  @JsonProperty("dbPath")
  public void setDbPath(String dbPath) {
    this.dbPath = dbPath;
  }

  @JsonProperty("baseUrl")
  public String getBaseUrl() {
    return baseUrl;
  }

  @JsonProperty("baseUrl")
  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @JsonProperty("passphrase")
  public String getPassphrase() {
    return passphrase;
  }

  @JsonProperty("passphrase")
  public void setPassphrase(String passphrase) {
    this.passphrase = passphrase;
  }

  @JsonProperty("certPassphrase")
  public String getCertPassphrase() {
    return certPassphrase;
  }

  @JsonProperty("certPassphrase")
  public void setCertPassphrase(String certPassphrase) {
    this.certPassphrase = certPassphrase;
  }

  @JsonProperty("keyPath")
  public String getKeyPath() {
    return keyPath;
  }

  @JsonProperty("keyPath")
  public void setKeyPath(String keyPath) {
    this.keyPath = keyPath;
  }

  @JsonProperty("certPath")
  public String getCertPath() {
    return certPath;
  }

  @JsonProperty("certPath")
  public void setCertPath(String certPath) {
    this.certPath = certPath;
  }

  @JsonProperty("admin")
  public String getAdmin() {
    return admin;
  }

  @JsonProperty("admin")
  public void setAdmin(String admin) {
    this.admin = admin;
  }

  @JsonProperty("adminPassword")
  public String getAdminPassword() {
    return adminPassword;
  }

  @JsonProperty("adminPassword")
  public void setAdminPassword(String adminPassword) {
    this.adminPassword = adminPassword;
  }

  @JsonProperty("debug")
  public boolean isDebug() {
    return debug;
  }

  @JsonProperty("debug")
  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  @JsonProperty("googleClientId")
  public String getGoogleClientId() {
    return googleClientId;
  }

  @JsonProperty("googleClientId")
  public void setGoogleClientId(String googleClientId) {
    this.googleClientId = googleClientId;
  }

  @JsonProperty("googleClientSecret")
  public String getGoogleClientSecret() {
    return googleClientSecret;
  }

  @JsonProperty("googleClientSecret")
  public void setGoogleClientSecret(String googleClientSecret) {
    this.googleClientSecret = googleClientSecret;
  }

  @JsonProperty("publicUrl")
  public String getPublicUrl() {
    return publicUrl;
  }

  @JsonProperty("publicUrl")
  public void setPublicUrl(String publicUrl) {
    this.publicUrl = publicUrl;
  }

  @JsonProperty("disableUserPassAuth")
  public boolean isDisableUserPassAuth() {
    return disableUserPassAuth;
  }

  @JsonProperty("disableUserPassAuth")
  public void setDisableUserPassAuth(boolean disableUserPassAuth) {
    this.disableUserPassAuth = disableUserPassAuth;
  }

  @JsonProperty("allowCsvDownload")
  public boolean isAllowCsvDownload() {
    return allowCsvDownload;
  }

  @JsonProperty("allowCsvDownload")
  public void setAllowCsvDownload(boolean allowCsvDownload) {
    this.allowCsvDownload = allowCsvDownload;
  }

  @JsonProperty("editorWordWrap")
  public boolean isEditorWordWrap() {
    return editorWordWrap;
  }

  @JsonProperty("editorWordWrap")
  public void setEditorWordWrap(boolean editorWordWrap) {
    this.editorWordWrap = editorWordWrap;
  }

  @JsonProperty("queryResultMaxRows")
  public int getQueryResultMaxRows() {
    return queryResultMaxRows;
  }

  @JsonProperty("queryResultMaxRows")
  public void setQueryResultMaxRows(int queryResultMaxRows) {
    this.queryResultMaxRows = queryResultMaxRows;
  }

  @JsonProperty("slackWebHook")
  public String getSlackWebHook() {
    return slackWebhook;
  }

  @JsonProperty("slackWebHook")
  public void setSlackWebHook(String slackWebHook) {
    this.slackWebhook = slackWebhook;
  }

  @JsonProperty("tableChartLinksRequireAuth")
  public boolean isTableChartLinksRequireAuth() {
    return tableChartLinksRequireAuth;
  }

  @JsonProperty("tableChartLinksRequireAuth")
  public void setTableChartLinksRequireAuth(boolean tableChartLinksRequireAuth) {
    this.tableChartLinksRequireAuth = tableChartLinksRequireAuth;
  }

  @JsonProperty("smtpFrom")
  public String getSmtpFrom() {
    return smtpFrom;
  }

  @JsonProperty("smtpFrom")
  public void setSmtpFrom(String smtpFrom) {
    this.smtpFrom = smtpFrom;
  }

  @JsonProperty("smtpHost")
  public String getSmtpHost() {
    return smtpHost;
  }

  @JsonProperty("smtpHost")
  public void setSmtpHost(String smtpHost) {
    this.smtpHost = smtpHost;
  }

  @JsonProperty("smtpPort")
  public String getSmtpPort() {
    return smtpPort;
  }

  @JsonProperty("smtpPort")
  public void setSmtpPort(String smtpPort) {
    this.smtpPort = smtpPort;
  }

  @JsonProperty("smtpSecure")
  public boolean isSmtpSecure() {
    return smtpSecure;
  }

  @JsonProperty("smtpSecure")
  public void setSmtpSecure(boolean smtpSecure) {
    this.smtpSecure = smtpSecure;
  }

  @JsonProperty("smtpUser")
  public String getSmtpUser() {
    return smtpUser;
  }

  @JsonProperty("smtpUser")
  public void setSmtpUser(String smtpUser) {
    this.smtpUser = smtpUser;
  }

  @JsonProperty("smtpPassword")
  public String getSmtpPassword() {
    return smtpPassword;
  }

  @JsonProperty("smtpPassword")
  public void setSmtpPassword(String smtpPassword) {
    this.smtpPassword = smtpPassword;
  }

  @JsonProperty("whiteListDomains")
  public String getWhitelistedDomains() {
    return whitelistedDomains;
  }

  @JsonProperty("whiteListDomains")
  public void setWhitelistedDomains(String whitelistedDomains) {
    this.whitelistedDomains = whitelistedDomains;
  }

  @JsonProperty("samlEntryPoint")
  public String getSamlEntryPoint() {
    return samlEntryPoint;
  }

  @JsonProperty("samlEntryPoint")
  public void setSamlEntryPoint(String samlEntryPoint) {
    this.samlEntryPoint = samlEntryPoint;
  }

  @JsonProperty("samlIssuer")
  public String getSamlIssuer() {
    return samlIssuer;
  }

  @JsonProperty("samlIssuer")
  public void setSamlIssuer(String samlIssuer) {
    this.samlIssuer = samlIssuer;
  }

  @JsonProperty("samlCallbackUrl")
  public String getSamlCallbackUrl() {
    return samlCallbackUrl;
  }

  @JsonProperty("samlCallbackUrl")
  public void setSamlCallbackUrl(String samlCallbackUrl) {
    this.samlCallbackUrl = samlCallbackUrl;
  }

  @JsonProperty("samlCert")
  public String getSamlCert() {
    return samlCert;
  }

  @JsonProperty("samlCert")
  public void setSamlCert(String samlCert) {
    this.samlCert = samlCert;
  }

  @JsonProperty("samlAuthContext")
  public String getSamlAuthContext() {
    return samlAuthContext;
  }

  @JsonProperty("samlAuthContext")
  public void setSamlAuthContext(String samlAuthContext) {
    this.samlAuthContext = samlAuthContext;
  }
}
