package io.tokern.bastion.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.tokern.bastion.core.auth.PasswordDigest;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

import java.nio.charset.StandardCharsets;
import java.security.Principal;

public class User implements Principal {
  public enum SystemRoles {
    ADMIN, DBADMIN, USER
  }
  public final int id;
  public final String name;
  public final String email;
  @JsonIgnore
  public final byte[] passwordHash;
  public final SystemRoles systemRole;
  public final int orgId;

  public User(int id, String name, String email, byte[] passwordHash, SystemRoles systemRole, int orgId) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.passwordHash = passwordHash;
    this.systemRole = systemRole;
    this.orgId = orgId;
  }

  @JsonCreator
  @JdbiConstructor
  public User(
      @JsonProperty("id") @ColumnName("id") int id,
      @JsonProperty("name") @ColumnName("name") String name,
      @JsonProperty("email") @ColumnName("email") String email,
      @JsonProperty("passwordHash") @ColumnName("password_hash") byte[] passwordHash,
      @JsonProperty("systemRole") @ColumnName("system_role") String systemRole,
      @JsonProperty("orgId") @ColumnName("org_id") int orgId) {
    this(id, name, email, passwordHash,
        systemRole == null ? SystemRoles.USER : SystemRoles.valueOf(systemRole),
        orgId);
  }

  public User(String name, String email, byte[] passwordHash, SystemRoles systemRole, int orgId) {
    this(0, name, email, passwordHash, systemRole, orgId);
  }

  public boolean login(String password) {
    return PasswordDigest.fromDigest(this.passwordHash).checkPassword(password.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public String getName() {
    return name;
  }
}
