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

    public static void init() {
        try {
            Context initContext = new InitialContext();

            System.out.println("Available mappings: ");
            for (String mapping : Mapping.getMappings()) {
                System.out.println("Mapping: " + mapping);
                System.out.println("Datasource: " + Mapping.getDatasourceJNDI(mapping));
                appDataSources.put(mapping, (DataSource) initContext.lookup(Mapping.getDatasourceJNDI(mapping)));
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection(String appCode) throws SQLException {
        DataSource dataSource = appDataSources.get(Mapping.findByCode(appCode));
        if (dataSource == null) {
            throw new SQLException("Datasource not found for app " + appCode);
        }
        return dataSource.getConnection();
    }
}