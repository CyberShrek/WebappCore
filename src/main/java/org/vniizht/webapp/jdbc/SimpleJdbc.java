package org.vniizht.webapp.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleJdbc {

    public static List<List<Object>> query(String sql) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columns = meta.getColumnCount();
            List<List<Object>> result = new ArrayList<>();

            while (rs.next()) {
                List<Object> row = new ArrayList<>(columns);
                for (int i = 1; i <= columns; i++) {
                    row.add(rs.getObject(i));
                }
                result.add(row);
            }
            return result;
        }
    }
}
