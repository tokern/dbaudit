package io.tokern.bastion.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.tokern.bastion.core.auth.PasswordDigest;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;

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

  @JdbiConstructor
  public User(
      @ColumnName("id") int id,
      @ColumnName("name") String name,
      @ColumnName("email") String email,
      @ColumnName("password_hash") byte[] passwordHash,
      @ColumnName("system_role") String systemRole,
      @ColumnName("org_id") int orgId) {
    this(id, name, email, passwordHash,
        systemRole == null ? SystemRoles.USER : SystemRoles.valueOf(systemRole),
        orgId);
  }

  @JsonCreator
  public User(
      @JsonProperty("id") int id,
      @JsonProperty("name") String name,
      @JsonProperty("email") String email,
      @JsonProperty("systemRole") String systemRole,
      @JsonProperty("orgId") int orgId) {
    this(id, name, email, null,
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

  public static class UserList {
    public final List<User> users;

    @JsonCreator
    public UserList(@JsonProperty("users") List<User> users) {
      this.users = users;
    }
  }

  public static class Request {
    public final String name;
    public final String email;
    public final String password;
    public final String systemRole;

    @JsonCreator
    public Request(@JsonProperty("name") String name,
            @JsonProperty("email") String email,
            @JsonProperty("password") String password,
            @JsonProperty("systemRole") String systemRole) {
      this.name = name;
      this.email = email;
      this.password = password;
      this.systemRole = systemRole;
    }
  }

  public static class PasswordChange {
    public final String currentPassword;
    public final String newPassword;

    @JsonCreator
    public PasswordChange(@JsonProperty("currentPassword") String currentPassword,
                          @JsonProperty("newPassword") String newPassword) {
      this.currentPassword = currentPassword;
      this.newPassword = newPassword;
    }
  }
}
