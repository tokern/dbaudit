package io.tokern.bastion.core.executor;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.tokern.bastion.api.Database;
import io.tokern.bastion.api.Query;
import io.tokern.bastion.db.QueryDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThreadPoolTest {
  static HealthCheckRegistry checkRegistry;
  static MetricRegistry metricRegistry;
  static Connections connections;
  static ThreadPool threadPool;

  @BeforeAll
  static void init() throws SQLException {
    checkRegistry = new HealthCheckRegistry();
    metricRegistry = new MetricRegistry();
    String encryptionSecret = "secret";
    Database database = new Database(1, "ThreadPoolTest", "jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", "",
        Database.encryptPassword("", encryptionSecret), Database.Driver.H2, 1);
    connections = new Connections(checkRegistry, metricRegistry, encryptionSecret);
    connections.addDatabase(database);
    System.setProperty("p6spy.config.appender", "com.p6spy.engine.spy.appender.Slf4JLogger");

    threadPool = new ThreadPool();
  }

  @Test
  void runWork() throws SQLException, InterruptedException, ExecutionException {
    Query query = new Query(1, "SELECT 1", 1, 1, 1, Query.State.WAITING);
    QueryDAO queryDAO = mock(QueryDAO.class);
    ThreadPool.Work work = new ThreadPool.Work(query, queryDAO, connections.getDataSource(1L).getConnection());

    Future<ThreadPool.Result> future = threadPool.getService().submit(work);

    ThreadPool.Result result = future.get();
    assertTrue(result.isSuccess());

    assertTrue(result.resultSet.next());
    assertEquals(1, result.resultSet.getInt(1));

    verify(queryDAO, times(1)).updateState(1, 1, Query.State.RUNNING);
    verify(queryDAO, times(1)).updateState(1, 1, Query.State.SUCCESS);
  }
}