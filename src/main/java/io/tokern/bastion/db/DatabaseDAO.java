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
  @SqlUpdate("insert into dbs(jdbc_url, user_name, password, type) values(:jdbcUrl, :userName, :password, :type)")
  Long insert(@BindFields Database database);

  @SqlUpdate("update dbs set jdbc_url=:jdbcUrl, user_name = :userName, password=:password, type=:type where id = :id")
  void update(@BindFields Database database);

  @SqlQuery("select id, jdbc_url, user_name, password, type from dbs")
  @RegisterConstructorMapper(Database.class)
  List<Database> list();

  @SqlQuery("select id, jdbc_url, user_name, password, type from dbs where id = ?")
  @RegisterConstructorMapper(Database.class)
  Database getById(long id);

  @SqlQuery("select id, jdbc_url, user_name, password, type from dbs where jdbc_url = ?")
  @RegisterConstructorMapper(Database.class)
  Database getByUrl(String jdbcUrl);

  @SqlUpdate("delete from dbs where id=?")
  void deleteById(long id);
}
