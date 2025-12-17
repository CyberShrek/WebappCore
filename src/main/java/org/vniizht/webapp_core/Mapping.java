package org.vniizht.webapp_core;

import org.vniizht.webapp_core.web.JSON;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Mapping {

    private static final Map<String, Map<String, String>> appProperties = new HashMap<>();

    public static Set<String> getMappings() {
        return appProperties.keySet();
    }

    public static boolean has(String mapping) {
        return appProperties.containsKey(mapping);
    }

    public static String getPage(String mapping) {
        Map<String, String> property = appProperties.get(mapping);
        if (property != null) {
            return property.get("page");
        }
        System.err.println("Property redirect not found for app: " + mapping);
        return null;
    }

    public static String getDatasourceJNDI(String mapping) {
        Map<String, String> property = appProperties.get(mapping);
        if (property != null) {
            return property.get("datasource-jndi");
        }
        System.err.println("Property datasource-jndi not found for app: " + mapping);
        return "";
    }

    public static String findByCode(String appCode) {
        for (Map.Entry<String, Map<String, String>> entry : appProperties.entrySet()) {
            if (entry.getValue().get("code").equals(appCode)) {
                return entry.getKey();
            }
        }
        return null;
    }

    static void load() {
        try {
            JSON.parse(Resources.load("mapping.json")).forEach((mapping, properties) -> {
                if (properties instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> typedProperties = (Map<String, String>) properties;
                    appProperties.put(mapping, typedProperties);
                } else {
                    throw new ClassCastException("Expected Map for app: " + mapping);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}