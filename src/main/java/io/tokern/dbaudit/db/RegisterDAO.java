package io.tokern.dbaudit.db;

import io.tokern.dbaudit.api.Organization;
import io.tokern.dbaudit.api.Register;
import io.tokern.dbaudit.api.User;
import io.tokern.dbaudit.core.auth.PasswordDigest;
import org.jdbi.v3.core.Jdbi;

public class RegisterDAO {
  public static void insert(Jdbi jdbi, Register register) {
    byte[] passwordHash = PasswordDigest.generateFromPassword(register.password)
        .getDigest();

    jdbi.useTransaction(h -> {
      UserDAO userDAO = h.attach(UserDAO.class);
      OrganizationDAO orgDao = h.attach(OrganizationDAO.class);
      Long orgId = orgDao.insert(new Organization(register.orgName, register.orgSlug));
      userDAO.insert(new User(register.userName, register.email, passwordHash, User.SystemRoles.ADMIN, orgId.intValue()));
    });
  }
}
