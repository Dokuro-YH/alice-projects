package com.yanhai.uaa.user;

import com.yanhai.core.resource.Queryable;
import com.yanhai.uaa.authentiaction.UserDetails;
import com.yanhai.uaa.user.exception.UserAlreadyExistsException;
import com.yanhai.uaa.user.exception.UserNotFoundException;

public interface UserServices extends Queryable<UserDetails> {

    UserDetails loadUserById(String id) throws UserNotFoundException;

    UserDetails loadUserByUsername(String username) throws UserNotFoundException;

    UserDetails addUser(UserDetails userDetails) throws UserAlreadyExistsException;

    UserDetails updateUser(UserDetails userDetails) throws UserNotFoundException;

    void removeUser(String id);
}
