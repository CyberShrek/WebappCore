package org.vniizht.webapp_core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Properties {

    public static final String USER_CHECK_REMOTE_NAME = "java:global/UCheck-1.0/UserCheck!com.vniizht.ucheck.UserCheckRemote";
    public static final String PRIL_INFO_REMOTE_NAME = "java:global/prilinfo-1.0/PrilInfo!org.vniizht.prilinfo.PrilInfoRemote";
    public static final String APPLICATION_CODE;
    public static final String DATASOURCE_JNDI;

    static {
        try {
            Map<String, String> properties = parseProperties(Resources.load("application.properties"));
            APPLICATION_CODE = properties.get("application.code");
            DATASOURCE_JNDI  = properties.get("datasource.jndi");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    static Map<String, String> parseProperties(String properties) {
        Map<String, String> result = new HashMap<>();
        if (properties == null || properties.isEmpty()) {
            return result;
        }

        // Разделяем строку с учетом различных символов конца строки
        String[] lines = properties.split("\\r?\\n");

        for (String line : lines) {
            String trimmedLine = line.trim();
            // Пропускаем пустые строки и комментарии
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                continue;
            }

            int separatorIndex = trimmedLine.indexOf('=');
            if (separatorIndex == -1) {
                // Если нет '=', рассматриваем всю строку как ключ с пустым значением
                result.put(trimmedLine, "");
            } else {
                // Извлекаем ключ и значение, убирая пробелы
                String key = trimmedLine.substring(0, separatorIndex).trim();
                String value = trimmedLine.substring(separatorIndex + 1).trim();
                result.put(key, value);
            }
        }

        return result;
    }
}