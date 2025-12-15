package org.vniizht.webapp_core;

import org.vniizht.webapp_core.web.JSON;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Configuration {

    public static final String USER_CHECK_REMOTE_NAME = "java:global/UCheck-1.0/UserCheck!com.vniizht.ucheck.UserCheckRemote";
    public static final String PRIL_INFO_REMOTE_NAME = "java:global/prilinfo-1.0/PrilInfo!org.vniizht.prilinfo.PrilInfoRemote";

    private static Map<String, Map<String, String>> appProperties = new HashMap<>();

    public static Set<String> getAppCodes() {
        return appProperties.keySet();
    }

    public static String getDatasourceJNDI(String appCode) {
        Map<String, String> property = appProperties.get(appCode);
        if (property != null) {
            return property.get("datasource-jndi");
        }
        System.err.println("Property datasource-jndi not found for app: " + appCode);
        return "";
    }

    static void load() {
        try {
            JSON.parse(Resources.load("apps.json")).forEach((appCode, properties) -> {
                if (properties instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> typedProperties = (Map<String, String>) properties;
                    appProperties.put(appCode, typedProperties);
                } else {
                    throw new ClassCastException("Expected Map for app: " + appCode);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}