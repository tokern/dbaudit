package io.tokern.bastion.db;

import io.tokern.bastion.api.Database;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface DatabaseDAO {
  @GetGeneratedKeys
  @SqlUpdate("insert into dbs(jdbc_url, user_name, password, type, org_id) " +
      "values(:jdbcUrl, :userName, :password, :type::db_type, :orgId)")
  Long insert(@BindFields Database database);

  @SqlUpdate("update dbs set jdbc_url=:jdbcUrl, user_name = :userName, password=:password, " +
      "type=:type::db_type where id = :id")
  void update(@BindFields Database database);

  @SqlQuery("select id, jdbc_url, user_name, password, type, org_id from dbs")
  @RegisterConstructorMapper(Database.class)
  List<Database> listAll();

  @SqlQuery("select id, jdbc_url, user_name, password, type, org_id from dbs where org_id = ?")
  @RegisterConstructorMapper(Database.class)
  List<Database> listByOrgId(int orgId);

  @SqlQuery("select id, jdbc_url, user_name, password, type, org_id from dbs where id = ? and org_id = ?")
  @RegisterConstructorMapper(Database.class)
  Database getById(long id, int orgId);

  @SqlQuery("select id, jdbc_url, user_name, password, type, org_id from dbs where jdbc_url = ? and org_id = ?")
  @RegisterConstructorMapper(Database.class)
  Database getByUrl(String jdbcUrl, int orgId);

  @SqlUpdate("delete from dbs where id=? and org_id = ?")
  void deleteById(long id, int orgId);
}
