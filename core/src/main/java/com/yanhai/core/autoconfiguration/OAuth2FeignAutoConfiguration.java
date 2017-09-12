package com.yanhai.core.autoconfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;

@Configuration
@ConditionalOnClass({Feign.class, RequestInterceptor.class})
public class OAuth2FeignAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("hystrix.shareSecurityContext")
    public RequestInterceptor oauth2RequestInterceptor() {
        return new RequestInterceptor() {

            private final static String authHeader = "Authorization";

            @Override
            public void apply(RequestTemplate template) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication != null && authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
                    OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();

                    template.header(authHeader, String.format("%s %s", details.getTokenType(), details.getTokenValue()));
                }
            }
        };
    }

}
