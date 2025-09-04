package org.vniizht.webapp_core;

import org.postgresql.core.QueryExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Resources {

    private static final Map<String, String> cache = new HashMap<>();

    public static boolean exists(String resource) {
        if (cache.containsKey(resource)) {
            return true;
        }
        try (InputStream is = getResourceAsStream(resource)) {
            return is != null;
        } catch (IOException e) {
            return false;
        }
    }

    public static String load(String resource) throws IOException {
        if (!cache.containsKey(resource)) {
            try (InputStream is = getResourceAsStream(resource)) {
                if (is == null) throw new IOException("Resource not found: " + resource);

                // Чтение файла для Java 8
                byte[] buffer = new byte[1024];
                int bytesRead;
                StringBuilder content = new StringBuilder();

                while ((bytesRead = is.read(buffer)) != -1) {
                    content.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
                }

                return content.toString();
            }
        }
        return cache.get(resource);
    }

    private static InputStream getResourceAsStream(String resource) throws IOException {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    }
}
