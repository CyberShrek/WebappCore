package org.vniizht.forge.webapp;
import org.vniizht.forge.webapp.model.Webapp;
import org.vniizht.forge.webapp.util.Resources;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.time.LocalDate;

@WebListener
public class Application implements ServletContextListener {

    public static LocalDate LAUNCH_DATE;
    public static Webapp WEBAPP;

    public static void main(String[] args) {
        LAUNCH_DATE = java.time.LocalDate.now();
        System.out.println("Initialized");
        try {
            System.out.println("Deserializing Webapp");
            WEBAPP = Resources.deserializeWebapp();
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        main(new String[0]);
    }

//    @Override
//    public void contextDestroyed(ServletContextEvent sce) {
//
//    }
}