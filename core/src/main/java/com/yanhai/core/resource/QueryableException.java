package com.yanhai.core.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class QueryableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public QueryableException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryableException(String message) {
        super(message);
    }

}
