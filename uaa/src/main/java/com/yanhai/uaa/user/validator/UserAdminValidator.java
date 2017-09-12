package com.yanhai.uaa.user.validator;

import com.yanhai.core.error.ValidateErrorException;
import com.yanhai.uaa.authentiaction.UserDetails;
import com.yanhai.uaa.user.BaseUserDetails;
import com.yanhai.uaa.user.UserServices;

public class UserAdminValidator implements UserValidator {

    private final UserServices userServices;

    public UserAdminValidator(UserServices userServices) {
        this.userServices = userServices;
    }

    @Override
    public UserDetails validate(UserDetails userDetails, Mode mode) throws ValidateErrorException {
        BaseUserDetails user = new BaseUserDetails(userDetails);

        UserDetails existsUser = userServices.loadUserByUsername(user.getUsername());

        if (existsUser != null) {
            throw new ValidateErrorException("用户名已存在: " + userDetails.getUsername());
        }

        return user;
    }
}
