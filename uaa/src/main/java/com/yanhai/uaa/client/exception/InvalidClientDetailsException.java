package com.yanhai.uaa.client.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidClientDetailsException extends RuntimeException {

    public InvalidClientDetailsException(String message) {
        super(message);
    }

    public InvalidClientDetailsException(String message, Throwable cause) {
        super(message, cause);
    }
}
