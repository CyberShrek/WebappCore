package org.vniizht.webapp_core.jdbc;

import org.vniizht.webapp_core.Mapping;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class ConnectionPool {
    private static final Map<String, DataSource> appDataSources = new HashMap<>();

    static {
        try {
            Context initContext = new InitialContext();

            for (String appPath : Mapping.getMappings()) {
                appDataSources.put(appPath, (DataSource) initContext.lookup(Mapping.getDatasourceJNDI(appPath)));
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection(String appPath) throws SQLException {
        DataSource dataSource = appDataSources.get(appPath);
        if (dataSource == null) {
            throw new SQLException("Datasource not found: " + appPath);
        }
        return appDataSources.get(appPath).getConnection();
    }
}