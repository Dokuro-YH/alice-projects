package com.yanhai.uaa.user;

import com.yanhai.uaa.authentiaction.UserDetails;
import com.yanhai.uaa.user.exception.UserNotFoundException;

public interface UserDatabase {

    UserDetails retrieveUserById(String id) throws UserNotFoundException;

    UserDetails retrieveUserByEmail(String email) throws UserNotFoundException;

    UserDetails retrieveUserByUsername(String username) throws UserNotFoundException;

    UserDetails retrieveUserByPhoneNumber(String phoneNumber) throws UserNotFoundException;

    void updateLastLogonTime(String id);
}
