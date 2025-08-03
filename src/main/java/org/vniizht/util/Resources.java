package org.vniizht.forge.webapp.util;

import org.vniizht.forge.webapp.model.Webapp;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class Resources {

    public static String read(String name) {
        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(name)) {

            if (inputStream == null) {
                throw new RuntimeException("File '" + name + "' not found in classpath!");
            }

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, bytesRead);
            }

            return result.toString(StandardCharsets.UTF_8.name());

        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + name, e);
        }
    }

    public static Webapp deserializeWebapp() {
        String filePath = "webapps.ser";

        try (InputStream is = Resources.class.getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new FileNotFoundException("File '" + filePath + "' not found in classpath!");
            }
            try (ObjectInputStream ois = new ObjectInputStream(is)) {
                Object obj = ois.readObject();
                // Проверка типа
                if (!(obj instanceof Webapp)) {
                    throw new IOException("Invalid type - expected Webapp");
                }
                return (Webapp) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deserialize webapp", e);
        }
    }
}
