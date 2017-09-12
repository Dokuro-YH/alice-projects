package com.yanhai.core.security;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.FilterInvocation;

public class UaaAdminOAuth2WebSecurityExpressionHandler extends OAuth2WebSecurityExpressionHandler {

    @Override
    protected StandardEvaluationContext createEvaluationContextInternal(Authentication authentication, FilterInvocation invocation) {
        StandardEvaluationContext ec = super.createEvaluationContextInternal(authentication, invocation);
        ec.setVariable("oauth2", new UaaAdminOAuth2SecurityExpressionMethods(authentication));
        return ec;
    }

}
