package io.tokern.bastion.core.Flyway;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FlywayFactory {
  @JsonProperty
  @NotEmpty
  private String encoding = StandardCharsets.UTF_8.name();
  @JsonProperty
  @NotNull
  private List<String> schemas = Collections.emptyList();
  @JsonProperty
  @NotNull
  private List<String> callbacks = Collections.emptyList();
  @JsonProperty
  @NotEmpty
  private String metaDataTableName = "flyway_schema_history";
  @JsonProperty
  @NotEmpty
  private String sqlMigrationPrefix = "V";
  @JsonProperty
  @NotEmpty
  private String sqlMigrationSeparator = "__";
  @JsonProperty
  @NotNull
  private List<String> sqlMigrationSuffixes = Collections.singletonList(".sql");
  @JsonProperty
  @NotEmpty
  private String placeholderPrefix = "${";
  @JsonProperty
  @NotEmpty
  private String placeholderSuffix = "}";
  @JsonProperty
  @NotNull
  private Map<String, String> placeholders = Collections.emptyMap();
  @JsonProperty
  @NotEmpty
  private List<String> locations = Collections.singletonList("db/migration");
  @JsonProperty
  @NotNull
  private List<String> resolvers = Collections.emptyList();
  @JsonIgnore
  private ClassLoader classLoader = null;
  @JsonProperty
  private boolean outOfOrder = false;
  @JsonProperty
  @NotNull
  private String baselineDescription = "<< Flyway Baseline >>";
  @JsonProperty
  private boolean baselineOnMigrate = false;
  @JsonProperty
  private boolean validateOnMigrate = true;
  @JsonProperty
  @NotBlank
  private String baseLineVersion = "1";
  @JsonProperty
  private boolean cleanDisabled = false;
  @JsonProperty
  private boolean group = false;
  @JsonProperty
  private boolean ignoreFutureMigrations = true;
  @JsonProperty
  private boolean ignoreIgnoredMigrations = false;
  @JsonProperty
  private boolean ignoreMissingMigrations = false;
  @JsonProperty
  @NotNull
  private String installedBy = "";
  @JsonProperty
  private boolean mixed = false;
  @JsonProperty
  private boolean placeholderReplacement = true;
  @JsonProperty
  private boolean skipDefaultCallbacks = false;
  @JsonProperty
  private boolean skipDefaultResolvers = false;

  /**
   * @see Flyway#getEncoding()
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * @see Flyway#setEncoding(String)
   */
  public void setEncoding(final String encoding) {
    this.encoding = encoding;
  }

  /**
   * @see Flyway#getSchemas()
   */
  public List<String> getSchemas() {
    return schemas;
  }

  /**
   * @see Flyway#setSchemas(String...)
   */
  public void setSchemas(final List<String> schemas) {
    this.schemas = schemas;
  }

  /**
   * @see Flyway#getCallbacks()
   */
  public List<String> getCallbacks() {
    return callbacks;
  }

  /**
   * @see Flyway#setCallbacks(Callback...)
   */
  public void setCallbacks(final List<String> callbacks) {
    this.callbacks = callbacks;
  }

  /**
   * @see Flyway#getTable()
   */
  public String getMetaDataTableName() {
    return metaDataTableName;
  }

  /**
   * @see Flyway#setTable(String)
   */
  public void setMetaDataTableName(final String metaDataTableName) {
    this.metaDataTableName = metaDataTableName;
  }

  /**
   * @see Flyway#getSqlMigrationPrefix()
   */
  public String getSqlMigrationPrefix() {
    return sqlMigrationPrefix;
  }

  /**
   * @see Flyway#setSqlMigrationPrefix(String)
   */
  public void setSqlMigrationPrefix(final String sqlMigrationPrefix) {
    this.sqlMigrationPrefix = sqlMigrationPrefix;
  }

  /**
   * @see Flyway#setSqlMigrationSuffixes(String...)
   */
  public List<String> getSqlMigrationSuffixes() {
    return sqlMigrationSuffixes;
  }

  /**
   * @see Flyway#setSqlMigrationSuffixes(String...)
   */
  public void setSqlMigrationSuffixes(final List<String> sqlMigrationSuffixes) {
    this.sqlMigrationSuffixes = sqlMigrationSuffixes;
  }

  /**
   * @see Flyway#getSqlMigrationSeparator()
   */
  public String getSqlMigrationSeparator() {
    return sqlMigrationSeparator;
  }

  /**
   * @see Flyway#setSqlMigrationSeparator(String)
   */
  public void setSqlMigrationSeparator(final String sqlMigrationSeparator) {
    this.sqlMigrationSeparator = sqlMigrationSeparator;
  }

  /**
   * @see Flyway#getPlaceholderPrefix()
   */
  public String getPlaceholderPrefix() {
    return placeholderPrefix;
  }

  /**
   * @see Flyway#setPlaceholderPrefix(String)
   */
  public void setPlaceholderPrefix(final String placeholderPrefix) {
    this.placeholderPrefix = placeholderPrefix;
  }

  /**
   * @see Flyway#getPlaceholderSuffix()
   */
  public String getPlaceholderSuffix() {
    return placeholderSuffix;
  }

  /**
   * @see Flyway#setPlaceholderSuffix(String)
   */
  public void setPlaceholderSuffix(final String placeholderSuffix) {
    this.placeholderSuffix = placeholderSuffix;
  }

  /**
   * @see Flyway#getPlaceholders()
   */
  public Map<String, String> getPlaceholders() {
    return placeholders;
  }

  /**
   * @see Flyway#setPlaceholders(Map)
   */
  public void setPlaceholders(final Map<String, String> placeholders) {
    this.placeholders = placeholders;
  }

  /**
   * @see Flyway#getLocations()
   */
  public List<String> getLocations() {
    return locations;
  }

  /**
   * @see Flyway#setLocations(String...)
   */
  public void setLocations(final List<String> locations) {
    this.locations = locations;
  }

  /**
   * @see Flyway#getResolvers()
   */
  public List<String> getResolvers() {
    return resolvers;
  }

  /**
   * @see Flyway#setResolvers(MigrationResolver...)
   */
  public void setResolvers(final List<String> resolvers) {
    this.resolvers = resolvers;
  }

  /**
   * @see Flyway#Flyway(ClassLoader)
   */
  public ClassLoader getClassLoader() {
    return classLoader;
  }

  /**
   * @see Flyway#Flyway(ClassLoader)
   */
  public void setClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  /**
   * @see Flyway#isOutOfOrder()
   */
  public boolean isOutOfOrder() {
    return outOfOrder;
  }

  /**
   * @see Flyway#setOutOfOrder(boolean)
   */
  public void setOutOfOrder(boolean outOfOrder) {
    this.outOfOrder = outOfOrder;
  }

  /**
   * @see Flyway#getBaselineDescription()
   */
  public String getBaselineDescription() {
    return baselineDescription;
  }

  /**
   * @see Flyway#setBaselineDescription(String)
   */
  public void setBaselineDescription(String baselineDescription) {
    this.baselineDescription = baselineDescription;
  }

  /**
   * @see Flyway#isBaselineOnMigrate()
   */
  public boolean isBaselineOnMigrate() {
    return baselineOnMigrate;
  }

  /**
   * @see Flyway#setBaselineOnMigrate(boolean)
   */
  public void setBaselineOnMigrate(boolean baselineOnMigrate) {
    this.baselineOnMigrate = baselineOnMigrate;
  }

  /**
   * @see Flyway#isValidateOnMigrate()
   */
  public boolean isValidateOnMigrate() {
    return validateOnMigrate;
  }

  /**
   * @see Flyway#setValidateOnMigrate(boolean)
   */
  public void setValidateOnMigrate(boolean validateOnMigrate) {
    this.validateOnMigrate = validateOnMigrate;
  }

  /**
   * @see Flyway#getBaselineVersion()
   */
  public String getBaseLineVersion() {
    return baseLineVersion;
  }

  /**
   * @see Flyway#setBaselineVersion(MigrationVersion)
   */
  public void setBaseLineVersion(String baseLineVersion) {
    this.baseLineVersion = baseLineVersion;
  }

  /**
   * @see Flyway#isCleanDisabled()
   */
  public boolean isCleanDisabled() {
    return cleanDisabled;
  }

  /**
   * @see Flyway#setCleanDisabled(boolean)
   */
  public void setCleanDisabled(boolean cleanDisabled) {
    this.cleanDisabled = cleanDisabled;
  }

  /**
   * @see Flyway#isGroup()
   */
  public boolean isGroup() {
    return group;
  }

  /**
   * @see Flyway#setGroup(boolean)
   */
  public void setGroup(boolean group) {
    this.group = group;
  }

  /**
   * @see Flyway#isIgnoreFutureMigrations()
   */
  public boolean isIgnoreFutureMigrations() {
    return ignoreFutureMigrations;
  }

  /**
   * @see Flyway#setIgnoreFutureMigrations(boolean)
   */
  public void setIgnoreFutureMigrations(boolean ignoreFutureMigrations) {
    this.ignoreFutureMigrations = ignoreFutureMigrations;
  }

  /**
   * @see Flyway#isIgnoreIgnoredMigrations()
   */
  public boolean isIgnoreIgnoredMigrations() {
    return ignoreIgnoredMigrations;
  }

  /**
   * @see Flyway#setIgnoreIgnoredMigrations(boolean)
   */
  public void setIgnoreIgnoredMigrations(boolean ignoreIgnoredMigrations) {
    this.ignoreIgnoredMigrations = ignoreIgnoredMigrations;
  }

  /**
   * @see Flyway#isIgnoreMissingMigrations()
   */
  public boolean isIgnoreMissingMigrations() {
    return ignoreMissingMigrations;
  }

  /**
   * @see Flyway#setIgnoreMissingMigrations(boolean)
   */
  public void setIgnoreMissingMigrations(boolean ignoreMissingMigrations) {
    this.ignoreMissingMigrations = ignoreMissingMigrations;
  }

  /**
   * @see Flyway#getInstalledBy()
   */
  public String getInstalledBy() {
    return installedBy;
  }

  /**
   * @see Flyway#setInstalledBy(String)
   */
  public void setInstalledBy(String installedBy) {
    this.installedBy = installedBy;
  }

  /**
   * @see Flyway#isMixed()
   */
  public boolean isMixed() {
    return mixed;
  }

  /**
   * @see Flyway#setMixed(boolean)
   */
  public void setMixed(boolean mixed) {
    this.mixed = mixed;
  }

  /**
   * @see Flyway#isPlaceholderReplacement()
   */
  public boolean isPlaceholderReplacement() {
    return placeholderReplacement;
  }

  /**
   * @see Flyway#setPlaceholderReplacement(boolean)
   */
  public void setPlaceholderReplacement(boolean placeholderReplacement) {
    this.placeholderReplacement = placeholderReplacement;
  }

  /**
   * @see Flyway#isSkipDefaultCallbacks()
   */
  public boolean isSkipDefaultCallbacks() {
    return skipDefaultCallbacks;
  }

  /**
   * @see Flyway#setSkipDefaultCallbacks(boolean)
   */
  public void setSkipDefaultCallbacks(boolean skipDefaultCallbacks) {
    this.skipDefaultCallbacks = skipDefaultCallbacks;
  }

  /**
   * @see Flyway#isSkipDefaultResolvers()
   */
  public boolean isSkipDefaultResolvers() {
    return skipDefaultResolvers;
  }

  /**
   * @see Flyway#setSkipDefaultResolvers(boolean)
   */
  public void setSkipDefaultResolvers(boolean skipDefaultResolvers) {
    this.skipDefaultResolvers = skipDefaultResolvers;
  }

  public Flyway build(final DataSource dataSource) {
    final String[] emptyStringArray = new String[0];

    FluentConfiguration flyway = getClassLoader() == null ? Flyway.configure() : Flyway.configure(getClassLoader());

    flyway = flyway.dataSource(dataSource)
        .baselineDescription(baselineDescription)
        .baselineOnMigrate(baselineOnMigrate)
        .baselineVersion(baseLineVersion)
        .callbacks(callbacks.toArray(emptyStringArray))
        .cleanDisabled(cleanDisabled)
        .encoding(encoding)
        .group(group)
        .ignoreFutureMigrations(ignoreFutureMigrations)
        .ignoreIgnoredMigrations(ignoreIgnoredMigrations)
        .ignoreMissingMigrations(ignoreMissingMigrations)
        .installedBy(installedBy)
        .locations(locations.toArray(emptyStringArray))
        .mixed(mixed)
        .outOfOrder(outOfOrder)
        .placeholderPrefix(placeholderPrefix)
        .placeholderReplacement(placeholderReplacement)
        .placeholderSuffix(placeholderSuffix)
        .placeholders(placeholders)
        .resolvers(resolvers.toArray(emptyStringArray))
        .schemas(schemas.toArray(emptyStringArray))
        .skipDefaultCallbacks(skipDefaultCallbacks)
        .skipDefaultResolvers(skipDefaultResolvers)
        .sqlMigrationPrefix(sqlMigrationPrefix)
        .sqlMigrationSeparator(sqlMigrationSeparator)
        .sqlMigrationSuffixes(sqlMigrationSuffixes.toArray(emptyStringArray))
        .table(metaDataTableName)
        .validateOnMigrate(validateOnMigrate);

    return flyway.load();
  }
}
