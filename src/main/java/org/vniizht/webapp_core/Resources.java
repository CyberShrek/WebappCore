package org.vniizht.webapp_core;

import org.postgresql.core.QueryExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Resources {

    public static String load(String resource) throws IOException {
        try (InputStream is = QueryExecutor.class.getClassLoader().getResourceAsStream(resource)) {
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
}
