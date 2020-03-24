package io.tokern.dbaudit.db;

import io.tokern.dbaudit.api.Query;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface QueryDAO {
  @GetGeneratedKeys
  @SqlUpdate("insert into queries(sql, user_id, db_id, org_id, state) " +
      "values(:sql, :userId, :dbId, :orgId, :state::query_state)")
  Long insert(@BindFields Query query);

  @SqlUpdate("update queries set state=:state::query_state where id = :id and org_id = :orgId")
  void updateState(@Bind("id") long id, @Bind("orgId") long orgId, @Bind("state") Query.State state);

  @SqlQuery("select id, sql, user_id, db_id, org_id, state from queries where org_id = ?")
  @RegisterConstructorMapper(Query.class)
  List<Query> listByOrg(long orgId);

  @SqlQuery("select id, sql, user_id, db_id, org_id, state from queries where user_id = ? and org_id = ?")
  @RegisterConstructorMapper(Query.class)
  List<Query> listByUser(long userId, long orgId);

  @SqlQuery("select id, sql, user_id, db_id, org_id, state from queries where id = ? and org_id = ?")
  @RegisterConstructorMapper(Query.class)
  Query getById(long id, long orgId);

  @SqlUpdate("delete from queries where id=? and org_id = ?")
  void deleteById(long id, int org_id);
}
