package org.vniizht.webapp_core.web.api;

import org.vniizht.webapp_core.exception.HttpException;
import org.vniizht.webapp_core.web.JSON;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class SimpleHttp {

    public static String getAppCode(HttpServletRequest request) {
        return request.getHeader("App-Code");
    }

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

        setupResponse(response);
        writeText(exception.getMessage(), response);
        exception.printStackTrace();
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

    private static String getFileExtension(String path) {
        int dotIndex = path.lastIndexOf('.');
        return (dotIndex == -1) ? "" : path.substring(dotIndex + 1).toLowerCase();
    }

    private static void write(String contentType, String content, HttpServletResponse response) throws IOException {
        write(contentType, content.getBytes("UTF-8"), response);
    }

    private static void write(String contentType, byte[] content, HttpServletResponse response) throws IOException {
        setupResponse(response);
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

    private static void setupResponse(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
    }
}
