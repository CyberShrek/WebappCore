package org.vniizht.forge.webapp.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.vniizht.forge.webapp.security.SqlValidator;
import org.vniizht.forge.webapp.util.JSON;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SqlService {

    // Execute parametrized query
    public static SimpleSet executeQuery(String query, Map<String, Object> params) throws SQLException {
        return executeQuery(query, params, null);
    }

    // Execute parametrized query with sub-sets whose will be transformed into sub-queries
    public static SimpleSet executeQuery(String query, Map<String, Object> params, SimpleSet mainSet) throws SQLException {
        try(Connection connection = JdbcProvider.getConnection()) {
            query = injectParams(query, params);
            if (mainSet != null)
                query = query.replace("#main", sqlSELECT(mainSet));
            SqlValidator.checkForInjections(query);
            return new SimpleSet(connection.createStatement().executeQuery(query));
        } catch (SQLException e) {
            throw new SQLException(query + "\n" + e.getMessage());
        }
    }

    public static String select(String sql) throws SQLException {
        try (PreparedStatement statement = JdbcProvider.getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getString(1); // Получаем значение первого столбца текущей строки
            } else {
                return ""; // Если результат пустой
            }
        }
    }

    // Transform formulas into SELECT expression, where each formula will represent one column,
    // then inject params into the generated expression and execute it.
    public static SimpleSet executeFormulas(List<String> formulas, Map<String, Object> params) throws SQLException {
        return executeQuery(sqlSELECT(formulas), params);
    }

    // Execute formulas using params and report. ExportableReport data will be transformed into "WITH report" statement.
    public static SimpleSet executeFormulas(List<String> formulas, Map<String, Object> params,
                                            SimpleSet resultSet) throws SQLException {
        return resultSet == null
                ? executeFormulas(formulas, params)
                : executeQuery(
                "WITH result AS (" + sqlSELECT(resultSet) + ") " + sqlSELECT(formulas) + " FROM result",
                params);
    }

    // Transform formulas into SELECT expression, where each formula will represent one column
    // Then inject params into the generated expression
    // ExportableReport data will be transformed into "WITH result" statement
    // Group data will be transformed into "WITH bunch" statement
    public static SimpleSet executeFormulas(List<String> formulas, Map<String, Object> params,
                                            SimpleSet resultSet, SimpleSet bunchSet) throws SQLException {
        return bunchSet == null
                ? executeFormulas(formulas, params, resultSet)
                : executeQuery(
                "WITH result AS (" + sqlSELECT(resultSet)
                        + "), bunch AS (" + sqlSELECT(bunchSet)
                        + ") " + sqlSELECT(formulas) 
                        + " FROM result FULL JOIN bunch USING (" + String.join(",", resultSet.getColumnNames()) + ")",
                params);
    }

    public static String injectParams(String sql, Map<String, Object> params) {
        String[] chunks = sql.split("\\$"); // split by $ symbol to get params in the start of each element and their indices
        for (int chunkIndex = 1; chunkIndex < chunks.length; chunkIndex++) { // the first element never has a param
            String chunk = chunks[chunkIndex];
            Matcher firstWordMatcher = Pattern.compile("^[a-zA-Z_]+\\w+").matcher(chunk);
            String firstWord = firstWordMatcher.find() ? firstWordMatcher.group() : "";
            String param = params.get(firstWord) == null ? "null" : Matcher.quoteReplacement(sqlfyObject(params.get(firstWord)));
            chunks[chunkIndex] = chunk.replaceFirst(
                    firstWord,
                    param);
        }
        return String.join("", chunks);
    }

    // Transform formulas into SELECT expression. All commas will be replaced with dots before joining
    private static String sqlSELECT(List<String> formulas) {
//        for (int i = 0; i < formulas.size() - 1; i++) {
//            formulas.set(i, formulas.get(i).replace(',', '.'));
//        }
        return "SELECT " + formulas.stream().map(formula -> formula.trim().isEmpty() ? "''" : formula).collect(Collectors.joining(","));
    }

    // Transform SimpleSet into SELECT expression. Union all rows into one query
    private static String sqlSELECT(SimpleSet simpleSet) {
        simpleSet.getData();
        String sqlSelect = sqlfyRow(
                simpleSet.getData().get(0),
                simpleSet.getColumnNames());

        StringBuilder sqlSelects = new StringBuilder(sqlSelect);
        for (int i = 1; i < simpleSet.getData().size(); i++) {
            sqlSelects
                    .append(" UNION ALL ")
                    .append(sqlfyRow(simpleSet.getData().get(i), null));
        }
        return sqlSelects.toString();
    }
    private static String sqlfyRow(List<Object> row, List<String> columnNames) {
        StringBuilder sqlSelect = new StringBuilder("SELECT ");
        for (int i = 0; i < row.size(); i++) {
            sqlSelect.append(sqlfyObject(row.get(i)));
            if(columnNames != null)
                sqlSelect.append(" AS ").append(columnNames.get(i));
            if (i < row.size() - 1)
                sqlSelect.append(",");
        }
        return sqlSelect.toString();
    }
    private static String sqlfyObject(Object object) {
        if(object instanceof String) // text, date. !!!UNSAFE!!!
            return "'" + object + "'";
        else if(object instanceof Number || object instanceof Boolean) // number, boolean
            return object.toString();
        else if(object instanceof List) // array
            return "ARRAY[" + ((List) object).stream().map(SqlService::sqlfyObject).collect(Collectors.joining(",")) + "]";
        else try { // json
            return "'" + JSON.stringify(object) + "'";
        } catch (JsonProcessingException e){
            return "'{\"error\": \"" + e.getMessage() + "\"}'";
        }
    }
}
