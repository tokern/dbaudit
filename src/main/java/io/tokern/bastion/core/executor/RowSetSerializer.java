package io.tokern.bastion.core.executor;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import javax.sql.RowSet;
import java.io.IOException;
import java.sql.*;

// Credit: https://stackoverflow.com/a/8120442
public class RowSetSerializer extends JsonSerializer<RowSet> {

  public static class ResultSetSerializerException extends JsonProcessingException {
    private static final long serialVersionUID = -914957626413580734L;

    public ResultSetSerializerException(Throwable cause){
      super(cause);
    }
  }

  @Override
  public Class<RowSet> handledType() {
    return RowSet.class;
  }

  @Override
  public void serialize(RowSet rs, JsonGenerator jgen, SerializerProvider provider)
      throws IOException, JsonProcessingException {

    try {
      ResultSetMetaData rsmd = rs.getMetaData();
      int numColumns = rsmd.getColumnCount();
      String[] columnNames = new String[numColumns];
      int[] columnTypes = new int[numColumns];

      for (int i = 0; i < columnNames.length; i++) {
        columnNames[i] = rsmd.getColumnLabel(i + 1);
        columnTypes[i] = rsmd.getColumnType(i + 1);
      }

      jgen.writeStartArray();

      while (rs.next()) {

        boolean b;
        long l;
        double d;

        jgen.writeStartObject();

        for (int i = 0; i < columnNames.length; i++) {

          jgen.writeFieldName(columnNames[i]);
          switch (columnTypes[i]) {

            case Types.INTEGER:
              l = rs.getInt(i + 1);
              if (rs.wasNull()) {
                jgen.writeNull();
              } else {
                jgen.writeNumber(l);
              }
              break;

            case Types.BIGINT:
              l = rs.getLong(i + 1);
              if (rs.wasNull()) {
                jgen.writeNull();
              } else {
                jgen.writeNumber(l);
              }
              break;

            case Types.DECIMAL:
            case Types.NUMERIC:
              jgen.writeNumber(rs.getBigDecimal(i + 1));
              break;

            case Types.FLOAT:
            case Types.REAL:
            case Types.DOUBLE:
              d = rs.getDouble(i + 1);
              if (rs.wasNull()) {
                jgen.writeNull();
              } else {
                jgen.writeNumber(d);
              }
              break;

            case Types.NVARCHAR:
            case Types.VARCHAR:
            case Types.LONGNVARCHAR:
            case Types.LONGVARCHAR:
              jgen.writeString(rs.getString(i + 1));
              break;

            case Types.BOOLEAN:
            case Types.BIT:
              b = rs.getBoolean(i + 1);
              if (rs.wasNull()) {
                jgen.writeNull();
              } else {
                jgen.writeBoolean(b);
              }
              break;

            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
              jgen.writeBinary(rs.getBytes(i + 1));
              break;

            case Types.TINYINT:
            case Types.SMALLINT:
              l = rs.getShort(i + 1);
              if (rs.wasNull()) {
                jgen.writeNull();
              } else {
                jgen.writeNumber(l);
              }
              break;

            case Types.DATE:
              provider.defaultSerializeDateValue(rs.getDate(i + 1), jgen);
              break;

            case Types.TIMESTAMP:
              provider.defaultSerializeDateValue(rs.getTime(i + 1), jgen);
              break;

            case Types.BLOB:
              Blob blob = rs.getBlob(i);
              provider.defaultSerializeValue(blob.getBinaryStream(), jgen);
              blob.free();
              break;

            case Types.CLOB:
              Clob clob = rs.getClob(i);
              provider.defaultSerializeValue(clob.getCharacterStream(), jgen);
              clob.free();
              break;

            case Types.ARRAY:
              throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type ARRAY");

            case Types.STRUCT:
              throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type STRUCT");

            case Types.DISTINCT:
              throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type DISTINCT");

            case Types.REF:
              throw new RuntimeException("ResultSetSerializer not yet implemented for SQL type REF");

            case Types.JAVA_OBJECT:
            default:
              provider.defaultSerializeValue(rs.getObject(i + 1), jgen);
              break;
          }
        }

        jgen.writeEndObject();
      }

      jgen.writeEndArray();

    } catch (SQLException e) {
      throw new ResultSetSerializerException(e);
    }
  }
}
