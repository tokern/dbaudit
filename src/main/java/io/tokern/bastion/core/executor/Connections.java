package io.tokern.bastion.core.executor;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.p6spy.engine.spy.P6DataSource;
import com.zaxxer.hikari.HikariDataSource;
import io.dropwizard.lifecycle.Managed;
import io.tokern.bastion.api.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.ws.rs.NotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Connections implements Managed {
  private static Logger logger = LoggerFactory.getLogger(Connections.class);
  private static int maxPoolSize = 3;

  private Map<Long, HikariDataSource> dataSourceMap;
  private final HealthCheckRegistry checkRegistry;
  private final MetricRegistry metricRegistry;
  private final String encryptionSecret;

  public Connections(HealthCheckRegistry checkRegistry, MetricRegistry metricRegistry, String encryptionSecret) {
    dataSourceMap = new HashMap<>();
    this.checkRegistry = checkRegistry;
    this.metricRegistry = metricRegistry;
    this.encryptionSecret = encryptionSecret;
  }

  @Override
  public void start() throws Exception {}

  @Override
  public void stop() throws Exception {
    dataSourceMap.forEach((id, dataSource) -> {dataSource.close();});
  }

  public void addDatabase(Database database) throws SQLException {
    if (dataSourceMap.containsKey(database.getId())) {
      throw new IllegalArgumentException(String.format("Connection already exists for database (%d, %s)",
          database.getId(), database.getJdbcUrl()));
    }

    DataSource dataSource = database.getDataSource(encryptionSecret);

    P6DataSource p6DataSource = new P6DataSource(dataSource);

    HikariDataSource hikari = new HikariDataSource();
    hikari.setPoolName(database.getName());
    hikari.setHealthCheckRegistry(this.checkRegistry);
    hikari.setMetricRegistry(this.metricRegistry);
    hikari.setDataSource(p6DataSource);
    hikari.setMaximumPoolSize(maxPoolSize);

    dataSourceMap.put(database.getId(), hikari);
    logger.info(String.format("Inserted database (%d)", database.getId()));
  }

  public HikariDataSource getDataSource(Long id) throws NotFoundException {
    if (dataSourceMap.containsKey(id)) {
      return dataSourceMap.get(id);
    }
    throw new NotFoundException(String.format("DataSource with id %d not found", id));
  }

  public void deleteDataSource(Long id) throws NotFoundException {
    if (dataSourceMap.containsKey(id)) {
      HikariDataSource dataSource = dataSourceMap.get(id);
      dataSource.close();
      dataSourceMap.remove(id);
    } else {
      throw new NotFoundException(String.format("DataSource with id %d not found", id));
    }
  }
}
