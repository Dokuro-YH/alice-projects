package com.yanhai.core.error;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ValidateErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ValidateErrorException(Errors errors) {
        super(converterErrors(errors));
    }

    public ValidateErrorException(String message) {
        super(message);
    }

    public ValidateErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidateErrorException(Throwable cause) {
        super(cause);
    }

    private static String converterErrors(Errors errors) {
        List<String> messages = new ArrayList<>();
        for (ObjectError error : errors.getAllErrors()) {
            messages.add(error.getDefaultMessage());
        }
        return String.join("\r\n", messages);
    }
}
