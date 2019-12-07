package io.tokern.bastion.core.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class RowSetSerializerTest {
  @Test
  void simpleSelectTest() throws SQLException, IOException {
    Connection conn = DriverManager.getConnection("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1", "","");
    Statement statement = conn.createStatement();
    ResultSet resultSet = statement.executeQuery("SELECT 1");

    CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
    rowSet.populate(resultSet);

    conn.close();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new RowSetModule());

    // Use the DataBind Api here
    ObjectNode objectNode = objectMapper.createObjectNode();

    // put the resultset in a containing structure
    objectNode.putPOJO("results", rowSet);

    // generate all
    StringWriter writer = new StringWriter();
    objectMapper.writeValue(writer, objectNode);
    String serialized = writer.toString();
    assertEquals("{\"results\":[{\"1\":1}]}", serialized);
  }
}