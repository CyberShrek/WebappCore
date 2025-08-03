package org.vniizht.forge.webapp.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class JdbcProvider {

    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource source;

    public synchronized static void setDebugMode(){
        System.out.println("Forged application JdbcProvider: Debug mode");
        configureCommons();
        config.setJdbcUrl("jdbc:postgresql://pg01exp.vniizht.lan:5432/abd");
        config.setUsername("asul");
        config.setPassword("ASULADMIN20202");
        source = new HikariDataSource(config);
    }

    public synchronized static void setDataSource(String jndiName){
        if(source != null && source.isRunning()){
            source.close();
        }
        configureCommons();
        config.setDataSourceJNDI(jndiName);
    }

    public static Connection getConnection() throws SQLException {
        if (source == null || !source.isRunning())
            setDebugMode();

        return source.getConnection();
    }

    private static void configureCommons(){
        config.setDriverClassName("org.postgresql.Driver");
        config.setMinimumIdle(16);
        config.setMaximumPoolSize(256);
        config.setIdleTimeout(10000);
    }
}
