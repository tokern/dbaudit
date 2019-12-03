package io.tokern.bastion.db;

import io.tokern.bastion.api.Organization;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OrganizationDAOTest {
  private static Jdbi jdbi;
  private static Handle handle;
  private static OrganizationDAO organizationDAO;
  private static Flyway flyway;

  private static String url = "jdbc:postgresql://localhost/bastiondb";
  private static String user = "bastion";
  private static String password = "passw0rd";
  private static String schema = "bastion_schema";

  @BeforeAll
  public static void registerDriver() throws ClassNotFoundException {
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
    handle.registerRowMapper(ConstructorMapper.factory(Organization.class));

    organizationDAO = handle.attach(OrganizationDAO.class);
 }

  @AfterAll
  public static void closeConnection() {
    flyway.clean();
    if (handle != null) {
      handle.close();
    }
  }

  @Test
  public void selectAll() {
    List<Organization> organizationList = organizationDAO.list();
    assertEquals(2, organizationList.size());
  }

  @Test
  public void selectByIdname() {
    Organization organization = organizationDAO.getByName("Tokern");
    assertEquals("https://tokern.io", organization.slug);

    Organization byId = organizationDAO.getById(organization.id);
    assertNotNull(byId);
  }

  @Test
  public void update() {
    Organization organization = organizationDAO.getByName("Tokern");

    Organization updated = new Organization(organization.id,
        organization.name,
        "https://tokern.io/bastion");

    organizationDAO.update(updated);

    Organization organizationNew = organizationDAO.getById(organization.id);
    assertEquals("https://tokern.io/bastion", organizationNew.slug);
  }

  public void delete() {
    Organization organization = organizationDAO.getByName("Google");

    organizationDAO.deleteById(organization.id);

    Organization daoById = organizationDAO.getById(organization.id);
    assertNull(daoById);
  }

  @Test
  public void createOrganization() {
    Long id = organizationDAO.insert(new Organization("Apple", "www.apple.com"));

    List<Map<String,Object>> rows = handle.select("select * from organizations where id=?", id)
        .mapToMap().list();

    assertFalse(rows.isEmpty());

    Map<String, Object> row = rows.get(0);
    assertEquals(id.intValue(), row.get("id"));
    assertEquals("Apple", row.get("name"));
    assertEquals("www.apple.com", row.get("slug"));
  }
}
