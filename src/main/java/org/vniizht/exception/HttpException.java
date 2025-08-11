package org.vniizht.exception;

import javax.servlet.ServletException;

public class HttpException extends ServletException {

    private final int code;
    public int getCode() {
        return code;
    }

    public HttpException(int code, String message) {
        super(message);
        this.code = code;
    }
}