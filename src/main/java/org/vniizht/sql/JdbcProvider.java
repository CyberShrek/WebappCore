package org.vniizht.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class JdbcProvider {

    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource source;

    public synchronized static void setDataSource(String jndiName){
        if(source != null && source.isRunning()){
            source.close();
        }
        configureCommons();
        config.setDataSourceJNDI(jndiName);
        source = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    private static void configureCommons(){
        config.setDriverClassName("org.postgresql.Driver");
        config.setMinimumIdle(16);
        config.setMaximumPoolSize(256);
        config.setIdleTimeout(10000);
    }
}
