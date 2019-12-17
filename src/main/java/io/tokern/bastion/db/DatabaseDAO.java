package io.tokern.bastion.db;

import io.tokern.bastion.api.Database;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface DatabaseDAO {
  @GetGeneratedKeys
  @SqlUpdate("insert into dbs(jdbc_url, name, user_name, password, type, org_id) " +
      "values(:jdbcUrl, :name, :userName, :password, :driver::db_type, :orgId)")
  Long insert(@BindBean Database database);

  @SqlUpdate("update dbs set name=:name, jdbc_url=:jdbcUrl, user_name = :userName, password=:password, " +
      "type=:driver::db_type where id = :id")
  void update(@BindBean Database database);

  @SqlQuery("select id, name, jdbc_url, user_name, password, type as driver, org_id from dbs")
  @RegisterConstructorMapper(Database.class)
  List<Database> listAll();

  @SqlQuery("select id, name, jdbc_url, user_name, password, type as driver, org_id from dbs where org_id = ?")
  @RegisterConstructorMapper(Database.class)
  List<Database> listByOrgId(int orgId);

  @SqlQuery("select id, name, jdbc_url, user_name, password, type as driver, org_id from dbs " +
      "where id = ? and org_id = ?")
  @RegisterConstructorMapper(Database.class)
  Database getById(long id, int orgId);

  @SqlQuery("select id, name, jdbc_url, user_name, password, type as driver, org_id from dbs " +
      "where jdbc_url = ? and org_id = ?")
  @RegisterConstructorMapper(Database.class)
  Database getByUrl(String jdbcUrl, int orgId);

  @SqlUpdate("delete from dbs where id=? and org_id = ?")
  void deleteById(long id, int orgId);
}
