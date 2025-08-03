package org.vniizht.forge.webapp.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleSet {
    private final List<String> columnNames;
    private final List<List<Object>> data;

    public SimpleSet()  {
        columnNames = new ArrayList<>();
        data = new ArrayList<>();
    }

    public SimpleSet(ResultSet rowSet) throws SQLException {
        columnNames = Arrays.asList(getColumnNames(rowSet.getMetaData()));
        data = getMatrixData(rowSet);
    }

    public SimpleSet(List<String> columnNames, List<List<Object>> rows) {
        this.columnNames = columnNames;
        this.data = rows;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<List<Object>> getData() {
        return data;
    }

    private static String[] getColumnNames(ResultSetMetaData metaData) throws SQLException {
        String[] columnNames = new String[metaData.getColumnCount()];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = metaData.getColumnName(i + 1);
        }
        return columnNames;
    }

    private static List<List<Object>> getMatrixData(ResultSet rowSet) throws SQLException {
        List<List<Object>> matrixData = new ArrayList<>();
        int columnCount = rowSet.getMetaData().getColumnCount();
        while (rowSet.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 0; i < columnCount; i++) {
                row.add(rowSet.getObject(i + 1));
            }
            matrixData.add(row);
        }
        return matrixData;
    }
}
