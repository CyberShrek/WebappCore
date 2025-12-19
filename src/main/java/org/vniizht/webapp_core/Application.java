package org.vniizht.webapp_core;

import org.vniizht.webapp_core.jdbc.ConnectionPool;

import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.time.LocalDate;

@WebListener
public class Application implements ServletContextListener {

    public static final String USER_CHECK_REMOTE_NAME = "java:global/UCheck-1.0/UserCheck!com.vniizht.ucheck.UserCheckRemote";
    public static final String PRIL_INFO_REMOTE_NAME = "java:global/prilinfo-1.0/PrilInfo!org.vniizht.prilinfo.PrilInfoRemote";

    public static LocalDate LAUNCH_DATE;
    public static String CONTEXT_ROOT;

    @Override
    public void contextInitialized(javax.servlet.ServletContextEvent servletContextEvent) {
        System.out.println("Loading mappings...");
        Mapping.load();
        System.out.println("Initializing connection pool...");
        ConnectionPool.init();
        LAUNCH_DATE = java.time.LocalDate.now();
        CONTEXT_ROOT = servletContextEvent.getServletContext().getContextPath();
        System.out.println("Application Initialized: " + CONTEXT_ROOT);
    }
}