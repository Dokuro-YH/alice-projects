package com.yanhai.core.autoconfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import com.yanhai.core.oauth.resource.UserInfoTokenServices;

@Configuration
@ConditionalOnClass({ResourceServerTokenServices.class, ResourceServerProperties.class})
public class OAuth2UserInfoServicesAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean({AuthorizationServerTokenServices.class, RemoteTokenServices.class})
    @ConditionalOnBean(ResourceServerProperties.class)
    public ResourceServerTokenServices userInfoTokenServices(ResourceServerProperties properties) {
        return new UserInfoTokenServices(properties.getUserInfoUri(), properties.getClientId());
    }
}
