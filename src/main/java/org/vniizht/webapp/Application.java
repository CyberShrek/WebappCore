package org.vniizht.webapp;

import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.time.LocalDate;

@WebListener
public class Application implements ServletContextListener {

    public static LocalDate LAUNCH_DATE;

    public static void main(String[] args) {
        LAUNCH_DATE = java.time.LocalDate.now();
        System.out.println("Initialized");
    }
}