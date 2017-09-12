package com.yanhai.core.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public interface SecurityContextAccessor {

    /**
     * @return true: 如果是客户端登录
     */
    boolean isClient();

    /**
     * @return true: 如果是用户登录
     */
    boolean isUser();

    /**
     * @return true: 如果拥有管理员角色
     */
    boolean isAdmin();

    /**
     * @return 当前用户的ID（可能为空）
     */
    String getUserId();

    /**
     * @return 当前用户的登录名（可能为空）
     */
    String getUserName();

    /**
     * @return 当前客户端ID（可能为空）
     */
    String getClientId();

    /**
     * @return 当前 用户/客户端 授权信息
     */
    String getAuthenticationInfo();

    /**
     * @return 当前权限集合（可能为空集合）
     */
    Collection<? extends GrantedAuthority> getAuthorities();

    /**
     * @return 当前作用域（可能为空集合）
     */
    Collection<String> getScopes();

}