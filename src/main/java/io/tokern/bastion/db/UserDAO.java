package io.tokern.bastion.db;

import io.tokern.bastion.api.User;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface UserDAO {
  @GetGeneratedKeys
  @SqlUpdate("insert into users(name, email, password_hash, system_role, org_id) " +
      "values(:name, :email, :passwordHash, :systemRole::system_role_type, :orgId)")
  Long insert(@BindFields User user);

  @SqlUpdate("update users set name=:name, email=:email, password_hash=:passwordHash, " +
      "system_role=:systemRole::system_role_type, org_id=:orgId where id = :id")
  void update(@BindFields User user);

  @SqlQuery("select id, name, email, password_hash, system_role, org_id from users where org_id = ?")
  @RegisterConstructorMapper(User.class)
  List<User> listByOrg(int orgId);

  @SqlQuery("select id, name, email, password_hash, system_role, org_id from users where email = ?")
  @RegisterConstructorMapper(User.class)
  User getByEmail(String email);

  @SqlQuery("select id, name, email, password_hash, system_role, org_id from users where id = ? and org_id = ?")
  @RegisterConstructorMapper(User.class)
  User getById(long id, int orgId);

  @SqlUpdate("delete from users where id=? and org_id = ?")
  void deleteById(long id, int org_id);
}
