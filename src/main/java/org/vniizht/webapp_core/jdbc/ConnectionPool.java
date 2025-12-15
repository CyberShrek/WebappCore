package org.vniizht.webapp_core.jdbc;

import org.vniizht.webapp_core.Configuration;

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

            for (String appCode : Configuration.getAppCodes()) {
                appDataSources.put(appCode, (DataSource) initContext.lookup(Configuration.getDatasourceJNDI(appCode)));
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection(String appCode) throws SQLException {
        DataSource dataSource = appDataSources.get(appCode);
        if (dataSource == null) {
            throw new SQLException("Datasource not found: " + appCode);
        }
        return appDataSources.get(appCode).getConnection();
    }
}