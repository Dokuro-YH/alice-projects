package com.yanhai.core.authentication;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author yanhai
 */
public enum UserAuthority implements GrantedAuthority {

    UAA_ADMIN("uaa.admin", 0), UAA_USER("uaa.user", 1), UAA_NONE("uaa.none", -1);

    public static final List<UserAuthority> USER_AUTHORITIES = Collections
            .unmodifiableList(Arrays.asList(UAA_USER));

    public static final List<UserAuthority> ADMIN_AUTHORITIES = Collections
            .unmodifiableList(Arrays.asList(UAA_ADMIN));

    public static final List<UserAuthority> NONE_AUTHORITIES = Collections
            .unmodifiableList(Arrays.asList(UAA_NONE));

    private final int value;

    private final String userType;

    private UserAuthority(String userType, int value) {
        this.userType = userType;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getUserType() {
        return userType;
    }

    @Override
    public String getAuthority() {
        return userType;
    }

}
