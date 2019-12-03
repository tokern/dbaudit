package io.tokern.bastion.db;

import io.tokern.bastion.api.Organization;
import io.tokern.bastion.api.Register;
import io.tokern.bastion.api.User;
import io.tokern.bastion.core.auth.PasswordDigest;
import org.jdbi.v3.core.Jdbi;

import java.nio.charset.StandardCharsets;

public class RegisterDAO {
  public static void insert(Jdbi jdbi, Register register) {
    byte[] passwordHash = PasswordDigest.generateFromPassword(register.password)
        .getDigest();

    jdbi.useTransaction(h -> {
      UserDAO userDAO = h.attach(UserDAO.class);
      OrganizationDAO orgDao = h.attach(OrganizationDAO.class);
      Long orgId = orgDao.insert(new Organization(register.orgName, register.orgSlug));
      userDAO.insert(new User(register.userName, register.email, passwordHash, orgId.intValue()));
    });
  }
}
