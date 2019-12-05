package io.tokern.bastion.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

public class Query {
  public enum State {
    WAITING, RUNNING, CANCELLED, ERROR, SUCCESS
  }

  public final long id;
  public final String sql;
  public final long userId;
  public final long dbId;
  public final long orgId;
  public final State state;

  public Query(long id, String sql, long userId, long dbId, long orgId, State state) {
    this.id = id;
    this.sql = sql;
    this.userId = userId;
    this.dbId = dbId;
    this.orgId = orgId;
    this.state = state;
  }

  @JdbiConstructor
  public Query(@ColumnName("id") long id,
               @ColumnName("sql") String sql,
               @ColumnName("user_id") long userId,
               @ColumnName("db_id") long dbId,
               @ColumnName("org_id") long orgId,
               @ColumnName("state") String state) {
    this(id, sql, userId, dbId, orgId,
        state == null ? null : State.valueOf(state));
  }

  @JsonCreator
  public Query(@JsonProperty("sql") String sql,
               @JsonProperty("userId") long userId,
               @JsonProperty("dbId") long dbId,
               @JsonProperty("orgId") long orgId,
               @JsonProperty("state") String state) {
    this(0, sql, userId, dbId, orgId, State.valueOf(state));
  }
}
