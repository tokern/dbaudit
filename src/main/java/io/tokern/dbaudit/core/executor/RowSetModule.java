package io.tokern.dbaudit.core.executor;

import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.module.SimpleModule;

import javax.sql.RowSet;

public class RowSetModule extends SimpleModule {
  private static final String NAME = "RowSetModule";

  public RowSetModule() {
    super(NAME, VersionUtil.versionFor(RowSetModule.class));
    addSerializer(RowSet.class, new RowSetSerializer());
  }
}
