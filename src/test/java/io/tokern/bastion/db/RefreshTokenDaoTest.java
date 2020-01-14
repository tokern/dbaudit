package io.tokern.bastion.db;

import io.tokern.bastion.api.RefreshToken;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RefreshTokenDaoTest {
  private static Jdbi jdbi;
  private static Handle handle;
  private static RefreshTokenDao refreshTokenDao;
  private static Flyway flyway;

  private static String url = "jdbc:postgresql://localhost/bastiondb";
  private static String user = "bastion";
  private static String password = "passw0rd";
  private static String schema = "bastion_schema";

  @BeforeAll
  static void registerDriver() throws ClassNotFoundException {
    flyway = Flyway.configure()
        .dataSource(url, user, password)
        .schemas(schema)
        .defaultSchema(schema)
        .locations("db/migration", "fixtures/flywayMigrations").load();
    flyway.migrate();

    Class.forName("org.postgresql.Driver");

    jdbi = Jdbi.create(url + "?currentSchema=" + schema, user, password);
    jdbi.installPlugin(new SqlObjectPlugin());
    handle = jdbi.open();
    handle.registerRowMapper(ConstructorMapper.factory(RefreshToken.class));

    refreshTokenDao = handle.attach(RefreshTokenDao.class);
  }

  @AfterAll
  static void closeConnection() {
    flyway.clean();
    if (handle != null) {
      handle.close();
    }
  }

  @Test
  void selectAll() {
    List<RefreshToken> tokenList = refreshTokenDao.listByUserId(1);
    assertEquals(2, tokenList.size());
  }

  @Test
  void testMemberVariables() {
    List<RefreshToken> tokenList = refreshTokenDao.listByUserId(2);
    assertEquals(1, tokenList.size());

    RefreshToken token = tokenList.get(0);
    assertEquals("fgh", token.token);
    assertEquals(2, token.userId);
    assertEquals(ZonedDateTime.of(
        LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC),
        token.createdAt.withZoneSameInstant(ZoneOffset.UTC));

    assertEquals(ZonedDateTime.of(
        LocalDateTime.of(2020, 1, 1, 1, 0, 0), ZoneOffset.UTC),
        token.expiresAt.withZoneSameInstant(ZoneOffset.UTC));

    assertFalse(token.forceInvalidated);
  }

  @Test
  void testInsert() {
    RefreshToken token = new RefreshToken("rope", 1, 1,
        ZonedDateTime.of(LocalDateTime.of(2020, 1, 10, 0, 0, 0), ZoneOffset.UTC),
        ZonedDateTime.of(LocalDateTime.of(2020, 1, 11, 0, 0, 0), ZoneOffset.UTC),
        false);

    long id = refreshTokenDao.insert(token);

    RefreshToken storedToken = refreshTokenDao.getById(id);
    assertEquals(token.token, storedToken.token);
    assertEquals(token.userId, storedToken.userId);
    assertEquals(token.createdAt, storedToken.createdAt.withZoneSameInstant(ZoneOffset.UTC));
    assertEquals(token.expiresAt, storedToken.expiresAt.withZoneSameInstant(ZoneOffset.UTC));
    assertEquals(token.forceInvalidated, storedToken.forceInvalidated);
  }

  @Test
  void testForceInvalidated() {
    refreshTokenDao.updateForceInvalidateById(true, 1);
    RefreshToken storedToken = refreshTokenDao.getById(1);

    assertTrue(storedToken.forceInvalidated);
  }

  @Test
  void delete() {
    assertNotNull(refreshTokenDao.getById(4));

    refreshTokenDao.deleteById(4);
    assertNull(refreshTokenDao.getById(4));
  }
}
