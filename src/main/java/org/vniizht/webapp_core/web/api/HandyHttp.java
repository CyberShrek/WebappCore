package org.vniizht.webapp_core.web.api;

import org.vniizht.webapp_core.exception.HttpException;
import org.vniizht.webapp_core.web.JSON;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class HandyHttp {

    public static String getAppCode(HttpServletRequest request) {
        String appCode = request.getHeader("App-Code");
        if (appCode == null || !appCode.matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("Invalid AppCode");
        }
        return appCode;
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
        else if (exception instanceof IllegalArgumentException)
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        else
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        setupResponse(response);
        writeText(exception.getMessage(), response);
        exception.printStackTrace();
    }

    // Parses object into JSON and writes it to the response
    public static void writeJson(Object object, HttpServletResponse response) throws IOException {
        write("application/json;charset=UTF-8", JSON.stringify(object), response);
    }

    public static void writeText(String text, HttpServletResponse response) throws IOException {
        write("text/plain;charset=UTF-8", text, response);
    }

    private static void write(String contentType, String content, HttpServletResponse response) throws IOException {
        String safeContent = Optional.ofNullable(content).orElse("")
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");

        byte[] bytes = safeContent.getBytes(StandardCharsets.UTF_8);

        setupResponse(response);
        response.setContentType(contentType);
        response.setContentLength(bytes.length);

        OutputStream os = response.getOutputStream();
        os.write(bytes);
        os.flush();
    }

    private static void setupResponse(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");

        // Важные заголовки безопасности
        response.setHeader("X-Content-Type-Options", "nosniff");

        // Усиленная CSP политика
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; " +
                        "script-src 'self'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data:; " +
                        "font-src 'self'; " +
                        "object-src 'none'; " +
                        "frame-src 'none'; " +
                        "base-uri 'self'; " +
                        "form-action 'self';");

        // Заголовки для защиты от XSS
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("X-Frame-Options", "DENY");

        response.setHeader("Cache-Control", "public, max-age=31536000, immutable");
    }
}
