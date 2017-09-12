package com.yanhai.core.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

public class UaaAdminOAuth2MethodSecurityExpressionHandler extends OAuth2MethodSecurityExpressionHandler {

    @Override
    public StandardEvaluationContext createEvaluationContextInternal(Authentication authentication, MethodInvocation mi) {
        StandardEvaluationContext ec = super.createEvaluationContextInternal(authentication, mi);
        ec.setVariable("oauth2", new UaaAdminOAuth2SecurityExpressionMethods(authentication));
        return ec;
    }

}
