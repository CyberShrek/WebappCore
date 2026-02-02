package org.vniizht.webapp_core.jdbc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.vniizht.webapp_core.Resources;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class HandyJdbc {

    public static boolean isQueryExists(String queryId) {
        return Resources.exists(getQueryPath(queryId));
    }

    private final static Map<String, App> appJdbs = new HashMap<>();
    public static App forApp(String appCode) {
        if (!appJdbs.containsKey(appCode)) {
            appJdbs.put(appCode, new App(appCode));
        }
        return appJdbs.get(appCode);
    }

    @AllArgsConstructor
    public static class App {
        final String appCode;
        public List<List<Object>> queryWithParams(String queryId, Map<String, Object> params) throws SQLException, IOException {
            String sqlTemplate = Resources.load(getQueryPath(queryId));

            // Безопасная подготовка SQL с валидацией
            PreparedSql preparedSql = prepareSql(sqlTemplate);

            try (Connection connection = ConnectionPool.getConnection(appCode);
                 PreparedStatement stmt = connection.prepareStatement(preparedSql.getSql())) {

                // Установка параметров
                int index = 1;
                for (String paramName : preparedSql.getParamNames()) {
                    Object value = params.get(paramName);
                    setParameter(stmt, index++, value);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    return resultSetToList(rs);
                }
            }
        }
    }

    @Getter
    private static class PreparedSql {
        private final String sql;
        private final List<String> paramNames;

        public PreparedSql(String sql, List<String> paramNames) {
            this.sql = sql;
            this.paramNames = Collections.unmodifiableList(paramNames);
        }
    }

    private static String getQueryPath(String queryId) {
        return "/sql/" + queryId + ".sql";
    }

    /**
     * Безопасная подготовка SQL с валидацией параметров
     */
    private static PreparedSql prepareSql(String sqlTemplate) {
        List<String> paramNames = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\$\\{(\\w+)}");
        Matcher matcher = pattern.matcher(sqlTemplate);
        StringBuffer result = new StringBuffer();

        // Регулярное выражение для безопасных имен параметров
        Pattern safeParamPattern = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");

        while (matcher.find()) {
            String paramName = matcher.group(1);

            // Валидация имени параметра
            if (!safeParamPattern.matcher(paramName).matches()) {
                throw new IllegalArgumentException("Invalid parameter name: '" + paramName + "'. Only alphanumeric characters and underscores are allowed.");
            }

            paramNames.add(paramName);
            matcher.appendReplacement(result, "?");
        }
        matcher.appendTail(result);

        return new PreparedSql(result.toString(), paramNames);
    }

    /**
     * Безопасная установка параметра с проверкой типа
     */
    private static void setParameter(PreparedStatement stmt, int index, Object value) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.NULL);
        } else if (value instanceof List) {
            setArrayParameter(stmt, index, (List<?>) value);
        } else if (value instanceof String) {
            stmt.setString(index, (String) value);
        } else if (value instanceof Integer) {
            stmt.setInt(index, (Integer) value);
        } else if (value instanceof Long) {
            stmt.setLong(index, (Long) value);
        } else if (value instanceof Double) {
            stmt.setDouble(index, (Double) value);
        } else if (value instanceof Boolean) {
            stmt.setBoolean(index, (Boolean) value);
        } else if (value instanceof java.util.Date) {
            stmt.setTimestamp(index, new java.sql.Timestamp(((java.util.Date) value).getTime()));
        } else if (value instanceof java.time.LocalDate) {
            stmt.setDate(index, java.sql.Date.valueOf((java.time.LocalDate) value));
        } else if (value instanceof java.time.LocalDateTime) {
            stmt.setTimestamp(index, java.sql.Timestamp.valueOf((java.time.LocalDateTime) value));
        } else {
            stmt.setObject(index, value);
        }
    }

    /**
     * Безопасная установка массива
     */
    private static void setArrayParameter(PreparedStatement stmt, int index, List<?> list) throws SQLException {
        if (list.isEmpty()) {
            // Пустой массив
            stmt.setArray(index, stmt.getConnection().createArrayOf("VARCHAR", new Object[0]));
            return;
        }

        // Определяем тип элементов
        Object firstElement = list.get(0);
        String sqlType;

        if (firstElement instanceof Integer) {
            sqlType = "INTEGER";
        } else if (firstElement instanceof Long) {
            sqlType = "BIGINT";
        } else if (firstElement instanceof Double) {
            sqlType = "DOUBLE PRECISION";
        } else if (firstElement instanceof Float) {
            sqlType = "REAL";
        } else if (firstElement instanceof Boolean) {
            sqlType = "BOOLEAN";
        } else if (firstElement instanceof String) {
            sqlType = "VARCHAR";
        } else {
            sqlType = "VARCHAR";
        }

        // Проверяем однородность типов в списке
        for (Object element : list) {
            if (element != null && !firstElement.getClass().isInstance(element)) {
                throw new IllegalArgumentException(
                        "Mixed types in list parameter at index " + index +
                                ". Expected " + firstElement.getClass().getSimpleName() +
                                ", found " + element.getClass().getSimpleName());
            }
        }

        Object[] array = list.toArray();
        Array sqlArray = stmt.getConnection().createArrayOf(sqlType, array);
        stmt.setArray(index, sqlArray);
    }

    private static List<List<Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<List<Object>> result = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();

        // Заголовки столбцов
        result.add(new ArrayList<>());
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            result.get(0).add(metaData.getColumnName(i));
        }

        // Строки
        while (rs.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                row.add(rs.getObject(i));
            }
            result.add(row);
        }

        return result;
    }
}
