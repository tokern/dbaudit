package io.tokern.bastion;

import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.tokern.bastion.api.Database;
import io.tokern.bastion.api.Organization;
import io.tokern.bastion.api.Query;
import io.tokern.bastion.api.User;
import io.tokern.bastion.core.auth.PasswordDigest;
import io.tokern.bastion.db.DatabaseDAO;
import io.tokern.bastion.db.OrganizationDAO;
import io.tokern.bastion.db.QueryDAO;
import io.tokern.bastion.db.UserDAO;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ApplicationDatabaseSetup extends DropwizardAppExtension.ServiceListener<BastionConfiguration> {
  private static Logger logger = LoggerFactory.getLogger(ApplicationDatabaseSetup.class);
  private Flyway flyway;

  public void onRun(BastionConfiguration configuration,
                    Environment environment, DropwizardAppExtension<BastionConfiguration> rule) throws Exception {
    logger.info("In DatabaseSetup OnRun");
    ManagedDataSource dataSource = configuration.getDataSourceFactory()
        .build(environment.metrics(), "flyway");
    flyway = configuration.getFlywayFactory().build(dataSource);
    flyway.migrate();

    Class.forName("org.postgresql.Driver");

    Jdbi jdbi = Jdbi.create(dataSource);
    jdbi.installPlugin(new SqlObjectPlugin());
    Long orgId = jdbi.withExtension(OrganizationDAO.class, dao -> dao.insert(new Organization("Tokern", "http://tokern.io")));
    Long adminId = jdbi.withExtension(UserDAO.class, dao -> dao.insert(new User(
        "tokern_root", "root@tokern.io",
        PasswordDigest.generateFromPassword("passw0rd").getDigest(),
        User.SystemRoles.ADMIN,
        orgId.intValue()
    )));

    Long dbAdminId = jdbi.withExtension(UserDAO.class, dao -> dao.insert(new User(
        "tokern_db", "db@tokern.io",
        PasswordDigest.generateFromPassword("passw0rd").getDigest(),
        User.SystemRoles.DBADMIN,
        orgId.intValue()
    )));

    Long userId = jdbi.withExtension(UserDAO.class, dao -> dao.insert(new User(
        "tokern_user", "user@tokern.io",
        PasswordDigest.generateFromPassword("passw0rd").getDigest(),
        User.SystemRoles.USER,
        orgId.intValue()
    )));

    jdbi.useExtension(UserDAO.class, dao -> dao.insert(new User(
        "tokern_put", "put@tokern.io",
        PasswordDigest.generateFromPassword("putw0rd").getDigest(),
        User.SystemRoles.USER,
        orgId.intValue()
    )));

    // Insert a few databases
    jdbi.useExtension(DatabaseDAO.class, dao -> dao.insert(new Database(
        "BastionDb",
        "jdbc:postgresql://localhost/bastiondb?currentSchema=bastion_app",
        "bastion",
        "passw0rd",
        "POSTGRESQL",
        orgId.intValue()
    )));

    jdbi.useExtension(DatabaseDAO.class, dao -> dao.insert(new Database(
        "Bastion2",
        "jdbc://localhost/bastion2",
        "bastion_user",
        "bastion_password",
        "MYSQL",
        orgId.intValue()
    )));

    //Insert a few queries
    jdbi.useExtension(QueryDAO.class, dao -> dao.insert(new Query(
        "select 1",
        userId,
        1,
        orgId,
        "WAITING"
    )));
    jdbi.useExtension(QueryDAO.class, dao -> dao.insert(new Query(
        "select 2",
        dbAdminId,
        1,
        orgId,
        "WAITING"
    )));
  }

  public void onStop(DropwizardAppExtension<BastionConfiguration> rule) throws Exception {
    logger.info("In DatabaseSetup OnStop");
    flyway.clean();
  }
}
