package io.tokern.bastion.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

public class Organization {
  public final int id;
  public final String name;
  public final String slug;

  @JsonCreator
  @JdbiConstructor
  public Organization(
      @JsonProperty("id") @ColumnName("id") int id,
      @JsonProperty("name") @ColumnName("name") String name,
      @JsonProperty("slug") @ColumnName("slug") String slug) {
    this.id = id;
    this.name = name;
    this.slug = slug;
  }

  public Organization(String name, String slug) {
    this(0, name, slug);
  }
}
