package org.vniizht.webapp_core.jdbc;

import org.vniizht.webapp_core.Resources;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SimpleJdbc {

    public static boolean isQueryExists(String queryId) {
        return Resources.exists(getQueryPath(queryId));
    }

    public static List<List<Object>> queryWithParams(String queryId, Map<String, Object> params) throws SQLException, IOException {

        String sql = Resources.load(getQueryPath(queryId));
        List<String> paramNames = new ArrayList<>();
        String preparedSql = replaceParams(sql, paramNames);

        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(preparedSql)) {
            setParams(stmt, paramNames, params);

            try (ResultSet rs = stmt.executeQuery()) {
                return resultSetToList(rs);
            }
        }
    }

    private static String getQueryPath(String queryId) {
        return "/sql/" + queryId + ".sql";
    }

    private static String replaceParams(String sql, List<String> paramNames) {
        sql = sql.replaceAll("\\?", "�");
        Pattern pattern = Pattern.compile("\\$\\{(\\w+)}");
        java.util.regex.Matcher matcher = pattern.matcher(sql);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            paramNames.add(matcher.group(1));
            matcher.appendReplacement(result, "?");
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static void setParams(PreparedStatement stmt, List<String> paramNames, Map<String, Object> params)
            throws SQLException {
        for (int i = 0; i < paramNames.size(); i++) {
            String paramName = paramNames.get(i);
            Object value = params.get(paramName);
            stmt.setObject(i + 1, value);
        }
    }

    private static List<List<Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<List<Object>> result = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            result.add(row);
        }

        return result;
    }
}
