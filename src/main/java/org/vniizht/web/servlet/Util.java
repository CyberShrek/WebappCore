package org.vniizht.forge.webapp.web.servlet;

import org.vniizht.forge.webapp.exception.HttpException;
import org.vniizht.forge.webapp.security.SqlValidator;
import org.vniizht.forge.webapp.util.JSON;
import org.vniizht.forge.webapp.sql.SimpleSet;
import org.vniizht.forge.webapp.sql.SqlService;
import org.vniizht.forge.webapp.util.Resources;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Util {

    /**
     * Parses the JSON body of the HTTP request into a key-value map.
     */
    public static Map<String, Object> parseJsonBody(HttpServletRequest request, HttpServletResponse responseIfFail) throws IOException {
        try {
            return JSON.parse(request.getInputStream());
        } catch (Exception exception){
            handleException(new HttpException(HttpServletResponse.SC_NOT_ACCEPTABLE, exception.getMessage()), responseIfFail);
            return new HashMap<>();
        }
    }

    /**
     * Handles an exception by setting the HTTP response code and writing an error message.
     */
    public static void handleException(Exception exception, HttpServletResponse response) throws IOException {
        if(exception instanceof HttpException)
            response.setStatus(((HttpException) exception).getCode());
        else
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        writeText(exception.getMessage(), response);
        exception.printStackTrace();
    }

    private static final Map<String, String> MIME_TYPES = new HashMap<>();
    static {
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("js", "application/javascript");
        MIME_TYPES.put("json", "application/json");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("ico", "image/x-icon");
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("woff2", "font/woff2");
    }
    // Writes text to the response
    public static void writeText(String text, HttpServletResponse response) throws IOException {
        write("text/plain", text, response);
    }

    // Parses object into JSON and writes it to the response
    public static void writeJson(Object object, HttpServletResponse response) throws IOException {
        write("application/json", JSON.stringify(object), response);
    }

    public static void writeHtml(String html, HttpServletResponse response) throws IOException {
        write("text/html", html, response);
    }

    public static void writeResource(String path, HttpServletResponse response) throws IOException {
        // Определяем MIME-тип по расширению файла
        String extension = getFileExtension(path);
        String contentType = MIME_TYPES.getOrDefault(extension, "application/octet-stream");

        // Записываем ресурс с правильным Content-Type
        write(contentType, Resources.read(path), response);
    }

    private static String getFileExtension(String path) {
        int dotIndex = path.lastIndexOf('.');
        return (dotIndex == -1) ? "" : path.substring(dotIndex + 1).toLowerCase();
    }

    public static void executeSqlRequest(HttpServletRequest request, String header, HttpServletResponse response) throws IOException {
        try {
            Map<String, Object> params = parseJsonBody(request, response);
            String expression = request.getHeader(header);
            expression = expression == null ? "null" : URLDecoder.decode(expression, "UTF-8");
            if (request.getRequestURI().startsWith("/appforge/") || SqlValidator.isTrusted(expression)) {
                SimpleSet result = header.equals("Query") ? SqlService.executeQuery(expression, params) : SqlService.executeFormulas(Arrays.asList(expression.split(",")), params);
                writeJson(result, response);
            }
        } catch (SQLException exception) {
            handleException(new HttpException(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage()), response);
        } catch (Exception e) {
            handleException(e, response);
        }
    }

    private static void write(String contentType, String content, HttpServletResponse response) throws IOException {
        write(contentType, content.getBytes("UTF-8"), response);
    }

    private static void write(String contentType, byte[] content, HttpServletResponse response) throws IOException {
        response.setContentType(contentType);
        response.setContentLength(content.length);

        // Критически важные заголовки для шрифтов
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Timing-Allow-Origin", "*");

        // Оптимизация кэширования
        response.setHeader("Cache-Control", "public, max-age=31536000, immutable");

        OutputStream os = response.getOutputStream();
        os.write(content);
        os.flush();
    }
}
