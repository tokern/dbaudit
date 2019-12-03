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
  @SqlUpdate("insert into users(name, email, password_hash, api_key, org_id) " +
      "values(:name, :email, :passwordHash, :apiKey, :orgId)")
  Long insert(@BindFields User user);

  @SqlUpdate("update users set name=:name, email=:email, password_hash=:passwordHash, " +
      "api_key=:apiKey, org_id=:orgId where id = :id")
  void update(@BindFields User user);

  @SqlQuery("select id, name, email, password_hash, api_key, org_id from users")
  @RegisterConstructorMapper(User.class)
  List<User> list();

  @SqlQuery("select id, name, email, password_hash, api_key, org_id from users where org_id = ?")
  @RegisterConstructorMapper(User.class)
  List<User> getByOrg(int orgId);

  @SqlQuery("select id, name, email, password_hash, api_key, org_id from users where email = ?")
  @RegisterConstructorMapper(User.class)
  User getByEmail(String email);

  @SqlQuery("select id, name, email, password_hash, api_key, org_id from users where id = ?")
  @RegisterConstructorMapper(User.class)
  User getById(long id);

  @SqlUpdate("delete from users where id=?")
  void deleteById(long id);
}
