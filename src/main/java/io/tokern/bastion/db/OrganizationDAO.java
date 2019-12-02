package io.tokern.bastion.db;

import io.tokern.bastion.api.Organization;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface OrganizationDAO {
  @GetGeneratedKeys
  @SqlUpdate("insert into organizations(name, slug) values(:name, :slug)")
  Long insert(@BindFields Organization organization);

  @SqlUpdate("update organizations set name=:name, slug=:slug where id = :id")
  void update(@BindFields Organization organization);

  @SqlQuery("select id, name, slug from organizations")
  @RegisterConstructorMapper(Organization.class)
  List<Organization> list();

  @SqlQuery("select id, name, slug from organizations where id = ?")
  @RegisterConstructorMapper(Organization.class)
  Organization getById(long id);

  @SqlQuery("select id, name, slug from organizations where name = ?")
  @RegisterConstructorMapper(Organization.class)
  Organization getByName(String jdbcUrl);

  @SqlUpdate("delete from organizations where id=?")
  void deleteById(long id);
}
