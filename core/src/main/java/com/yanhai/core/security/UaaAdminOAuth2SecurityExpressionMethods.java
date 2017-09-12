package com.yanhai.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2SecurityExpressionMethods;

public class UaaAdminOAuth2SecurityExpressionMethods extends OAuth2SecurityExpressionMethods {

    public UaaAdminOAuth2SecurityExpressionMethods(Authentication authentication) {
        super(authentication);
    }

    private boolean isUaaAdmin() {
        return super.hasAnyScope("uaa.admin");
    }

    @Override
    public boolean clientHasAnyRole(String... roles) {
        return isUaaAdmin() || super.clientHasAnyRole(roles);
    }

    @Override
    public boolean hasAnyScope(String... scopes) {
        return isUaaAdmin() || super.hasAnyScope(scopes);
    }

    @Override
    public boolean hasAnyScopeMatching(String... scopesRegex) {
        return isUaaAdmin() || super.hasAnyScopeMatching(scopesRegex);
    }
}
