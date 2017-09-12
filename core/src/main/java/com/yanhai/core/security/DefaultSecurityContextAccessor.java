package com.yanhai.core.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2ExpressionUtils;

import com.yanhai.core.authentication.UserPrincipal;

public class DefaultSecurityContextAccessor implements SecurityContextAccessor {

    @Override
    public boolean isClient() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();

        if (!(a instanceof OAuth2Authentication)) {
            return false;
        }

        return ((OAuth2Authentication) a).isClientOnly();
    }

    @Override
    public boolean isUser() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();

        if (a instanceof OAuth2Authentication) {
            return !isClient();
        }

        if (a != null && a.getPrincipal() instanceof UserPrincipal) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isAdmin() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        String[] adminRoles = new String[]{"uaa.admin"};
        if (a == null) {
            return false;
        }

        boolean result;
        if (a instanceof OAuth2Authentication) {
            OAuth2Authentication oa = (OAuth2Authentication) a;
            result = OAuth2ExpressionUtils.hasAnyScope(oa, adminRoles);
        } else {
            result = hasAnyAdminScope(a, adminRoles);
        }

        if (!result) {
            UaaAdminOAuth2SecurityExpressionMethods eval = new UaaAdminOAuth2SecurityExpressionMethods(a);
            result = eval.hasAnyScope(adminRoles);
        }

        return result;
    }

    private boolean hasAnyAdminScope(Authentication a, String... adminRoles) {
        Set<String> authorities = (a == null ? Collections.emptySet() : AuthorityUtils.authorityListToSet(a.getAuthorities()));
        for (String s : adminRoles) {
            if (authorities.contains(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getUserId() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a == null ? null : ((UserPrincipal) a.getPrincipal()).getId();
    }

    @Override
    public String getUserName() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a == null ? null : a.getName();
    }

    @Override
    public String getAuthenticationInfo() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();

        if (a instanceof OAuth2Authentication) {
            OAuth2Authentication oauth = ((OAuth2Authentication) a);

            String info = getClientId();
            if (!oauth.isClientOnly()) {
                info = info + "; " + a.getName() + "; " + getUserId();
            }

            return info;
        } else {
            return a.getName();
        }
    }

    @Override
    public String getClientId() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();

        if (!(a instanceof OAuth2Authentication)) {
            return null;
        }

        return ((OAuth2Authentication) a).getOAuth2Request().getClientId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a == null ? Collections.emptySet() : a.getAuthorities();
    }

    @Override
    public Collection<String> getScopes() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (!(a instanceof OAuth2Authentication)) {
            return Collections.emptySet();
        }

        return ((OAuth2Authentication) a).getOAuth2Request().getScope();
    }
}