package org.vniizht.webapp_core.exception;

import lombok.Getter;

import javax.servlet.ServletException;

@Getter
public class HttpException extends ServletException {

    private final int code;

    public HttpException(int code, String message) {
        super(message);
        this.code = code;
    }
}