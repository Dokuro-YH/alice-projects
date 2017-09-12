package com.yanhai.core.oauth.resource;

import java.util.Map;

import org.springframework.security.oauth2.provider.OAuth2Request;

public interface OAuth2RequestExtractor {

    OAuth2Request extractOAuth2Request(Map<String, Object> map);
}
