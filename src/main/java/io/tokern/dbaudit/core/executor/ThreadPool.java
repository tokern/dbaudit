package io.tokern.dbaudit.core.executor;

import io.dropwizard.lifecycle.Managed;
import io.tokern.dbaudit.api.Query;
import io.tokern.dbaudit.db.QueryDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.*;

public class ThreadPool implements Managed {
  private final ExecutorService service;

  public ThreadPool() {
    service = new ThreadPoolExecutor(10, 100, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
  }

  @Override
  public void start() throws Exception {}

  @Override
  public void stop() throws Exception {
    if (!service.isShutdown()) {
      service.shutdown();
      if (!service.awaitTermination(10, TimeUnit.SECONDS)) {
        service.shutdownNow();
      }
    }
  }

  public ExecutorService getService() {
    return service;
  }

  public static class Work implements Callable<Result> {
    private static Logger logger = LoggerFactory.getLogger(Work.class);

    private final Query query;
    private final QueryDAO queryDAO;
    private final Connection connection;

    public Work(Query query, QueryDAO dao, Connection connection) {
      this.query = query;
      this.queryDAO = dao;
      this.connection = connection;
    }

    @Override
    public Result call() throws Exception {
      queryDAO.updateState(query.id, query.orgId, Query.State.RUNNING);
      try (
          Statement statement = this.connection.createStatement();
          ResultSet resultSet = statement.executeQuery(query.sql);
          ){
        CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
        rowSet.populate(resultSet);
        queryDAO.updateState(query.id, query.orgId, Query.State.SUCCESS);
        logger.info(String.format("Query {%d} completed successfully", query.id));
        return new Result(rowSet, null);
      } catch (SQLException sql_exc) {
        queryDAO.updateState(query.id, query.orgId, Query.State.ERROR);
        logger.warn(String.format("Query {%d} failed with message {%s}", query.id, sql_exc.getMessage()));
        return new Result(null, sql_exc);
      } finally {
        if (connection != null) {
          connection.close();
        }
      }
    }
  }

  public static class Result {
    public final CachedRowSet resultSet;
    public final Throwable throwable;

    public Result(CachedRowSet resultSet, Throwable throwable) {
      this.resultSet = resultSet;
      this.throwable = throwable;
    }

    public boolean isSuccess() {
      return resultSet != null;
    }
  }
}
