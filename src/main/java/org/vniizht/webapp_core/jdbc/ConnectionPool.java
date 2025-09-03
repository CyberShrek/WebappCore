package org.vniizht.webapp_core.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.vniizht.webapp_core.Properties;

import java.sql.Connection;
import java.sql.SQLException;

abstract class ConnectionPool {

    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource source;

    static {
        config.setDataSourceJNDI(Properties.DATASOURCE_JNDI);
//        config.setJdbcUrl("jdbc:postgresql://pg01exp.vniizht.lan:5432/abd");
//        config.setUsername("asul");
//        config.setPassword("ASULADMIN20202");
//        config.setDriverClassName("org.postgresql.Driver");
        config.setMinimumIdle(16);
        config.setMaximumPoolSize(256);
        config.setIdleTimeout(10000);
        source = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return source.getConnection();
    }
}
