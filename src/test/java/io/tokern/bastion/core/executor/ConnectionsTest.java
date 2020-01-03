package io.tokern.bastion.core.executor;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.tokern.bastion.api.Database;
import org.h2.store.Data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.NotFoundException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionsTest {
  static HealthCheckRegistry checkRegistry;
  static MetricRegistry metricRegistry;
  static Connections connections;
  static String encryptionSecret = "secret";

  @BeforeAll
  static void initMocks() throws SQLException {
    checkRegistry = new HealthCheckRegistry();
    metricRegistry = new MetricRegistry();
    Database database = new Database(1, "ConnectionsTest", "jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", "",
        Database.encryptPassword("", encryptionSecret), Database.Driver.H2, 1);
    connections = new Connections(checkRegistry, metricRegistry, encryptionSecret);
    connections.addDatabase(database);
    System.setProperty("p6spy.config.appender", "com.p6spy.engine.spy.appender.Slf4JLogger");
  }

  @Test
  void getTest() {
    assertNotNull(connections.getDataSource(1L));
  }

  @Test
  void getUnknownTest() {
    assertThrows(NotFoundException.class, () -> connections.getDataSource(100L));
  }

  @Test
  void addTest() throws SQLException {
    Database d2 = new Database(2, "addTest", "jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", "",
        Database.encryptPassword("", encryptionSecret), Database.Driver.H2, 1);
    connections.addDatabase(d2);
    assertNotNull(connections.getDataSource(2L));
  }

  @Test
  void disAllowDuplicate() {
    Database d1 = new Database(1, "disAllowDuplicateTest", "jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", "", "", Database.Driver.H2, 1);
    assertThrows(IllegalArgumentException.class, () -> connections.addDatabase(d1));
  }

  @Test
  void deleteTest() throws SQLException {
    Database d3 = new Database(3, "deleteTest", "jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", "",
        Database.encryptPassword("", encryptionSecret), Database.Driver.H2, 1);
    connections.addDatabase(d3);
    assertDoesNotThrow(() -> connections.deleteDataSource(3L));
  }

  @Test
  void deleteUnknownTest() {
    assertThrows(NotFoundException.class, () -> connections.deleteDataSource(100L));
  }

  @Test
  void runQueries() throws SQLException {
    Connection connection = connections.getDataSource(1L).getConnection();
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery("SELECT 1");
    assertTrue(resultSet.next());
    assertEquals(1, resultSet.getInt(1));
  }
}