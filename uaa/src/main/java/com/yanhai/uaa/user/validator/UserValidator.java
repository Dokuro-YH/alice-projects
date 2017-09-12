package com.yanhai.uaa.user.validator;

import com.yanhai.core.error.ValidateErrorException;
import com.yanhai.uaa.authentiaction.UserDetails;

public interface UserValidator {

    UserDetails validate(UserDetails userDetails, Mode mode) throws ValidateErrorException;

    enum Mode {
        CREATE, MODIFY, DELETE;
    }
}
