package com.yanhai.core.authentication;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;

public interface UserPrincipal extends Principal {

    String getId();

    Collection<? extends GrantedAuthority> getAuthorities();

    Map<String, Object> getAdditionalInformation();
}