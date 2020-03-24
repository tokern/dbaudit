package io.tokern.dbaudit.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GitStateTest {
  private static String json = "{\n"
      + "\"git.branch\" : \"json-response\",\n"
      + "\"git.build.host\" : \"build-machine\",\n"
      + "\"git.build.time\" : \"2018-11-22T22:33:26+0530\",\n"
      + "\"git.build.user.email\" : \"xxxxxx@yyyyyyy.io\",\n"
      + "\"git.build.user.name\" : \"User Name\",\n"
      + "\"git.build.version\" : \"0.4.3-SNAPSHOT\",\n"
      + "\"git.closest.tag.commit.count\" : \"\",\n"
      + "\"git.closest.tag.name\" : \"\",\n"
      + "\"git.commit.id\" : \"28010b73bae868ba252fbf2974f93d30ae189ea0\",\n"
      + "\"git.commit.id.abbrev\" : \"28010b7\",\n"
      + "\"git.commit.id.describe\" : \"28010b7-dirty\",\n"
      + "\"git.commit.id.describe-short\" : \"28010b7-dirty\",\n"
      + "\"git.commit.message.full\" : \"new:usr:Improve Dblint resource parameters and "
        + "responses\\n\\nUse classes (SqlQuery and QueryResponse) instead of strings for\\nREST "
        + "calls to digest and pretty print.\",\n"
      + "\"git.commit.message.short\" : \"new:usr:Improve Dblint resource parameters and "
        + "responses\",\n"
      + "\"git.commit.time\" : \"2018-11-22T21:24:13+0530\",\n"
      + "\"git.commit.user.email\" : \"xxxxx@xxxxx.io\",\n"
      + "\"git.commit.user.name\" : \"User Name\",\n"
      + "\"git.dirty\" : \"true\",\n"
      + "\"git.remote.origin.url\" : \"git@github.com:vrajat/mart.git\",\n"
      + "\"git.tags\" : \"\",\n"
      + "\"git.total.commit.count\" : \"94\"\n"
      + "}";

  @Test
  void deSerialize() throws IOException {
    GitState gitState = new ObjectMapper().readValue(json, GitState.class);
    assertEquals("0.4.3-SNAPSHOT", gitState.buildVersion);
    assertEquals("28010b7-dirty", gitState.describe);
  }
}
