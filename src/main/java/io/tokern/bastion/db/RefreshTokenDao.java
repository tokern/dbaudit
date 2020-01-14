package io.tokern.bastion.db;

import io.tokern.bastion.api.RefreshToken;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface RefreshTokenDao {
  @GetGeneratedKeys
  @SqlUpdate("insert into refresh_tokens(token, user_id, org_id, created_at, expires_at, force_invalidated) " +
      "values(:token, :userId, :orgId, :createdAt, :expiresAt, :forceInvalidated)")
  Long insert(@BindFields RefreshToken refreshToken);

  @SqlQuery("select id, token, user_id, org_id, created_at, expires_at, force_invalidated " +
      "from refresh_tokens where id = ?")
  @RegisterConstructorMapper(RefreshToken.class)
  RefreshToken getById(long id);

  @SqlQuery("select id, token, user_id, org_id, created_at, expires_at, force_invalidated " +
      "from refresh_tokens where token = ?")
  @RegisterConstructorMapper(RefreshToken.class)
  RefreshToken getByToken(String token);

  @SqlQuery("select id, token, user_id, org_id, created_at, expires_at, force_invalidated " +
      "from refresh_tokens where user_id = ?")
  @RegisterConstructorMapper(RefreshToken.class)
  List<RefreshToken> listByUserId(int userId);

  @SqlUpdate("update refresh_tokens set force_invalidated = ? where id = ?")
  void updateForceInvalidateById(boolean forceInvalidated, int id);

  @SqlUpdate("delete from refresh_tokens where id=?")
  void deleteById(long id);
}
