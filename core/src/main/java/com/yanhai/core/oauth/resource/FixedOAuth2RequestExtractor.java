package com.yanhai.core.oauth.resource;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedAuthoritiesExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.util.ObjectUtils;

public class FixedOAuth2RequestExtractor implements OAuth2RequestExtractor {

    private static final String OAUTH2_REQUEST_KEY = "oauth2Request";

    private static final String SCOPE_KEY = "scope";

    private static final String RESOURCE_IDS_KEY = "resourceIds";

    private final String clientId;

    private AuthoritiesExtractor authoritiesExtractor = new FixedAuthoritiesExtractor();

    public FixedOAuth2RequestExtractor(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public OAuth2Request extractOAuth2Request(Map<String, Object> map) {

        @SuppressWarnings("unchecked")
        Map<String, Object> request = (Map<String, Object>) map.get(OAUTH2_REQUEST_KEY);

        if (request == null) {
            return new OAuth2Request(null, this.clientId, null, true, null,
                    null, null, null, null);
        }

        Set<String> scope = asSet(request.get(SCOPE_KEY));
        Set<String> resourceIds = asSet(request.get(RESOURCE_IDS_KEY));
        List<GrantedAuthority> authorities = authoritiesExtractor.extractAuthorities(request);

        // 删除 oauth2Request 防止下游获取敏感信息
        map.remove(OAUTH2_REQUEST_KEY);

        return new OAuth2Request(null, this.clientId, authorities, true, 
                scope.isEmpty() ? null : scope,
                resourceIds.isEmpty() ? null : resourceIds,
                null, null, null);
    }

    private Set<String> asSet(Object object) {
        Set<String> scope = new LinkedHashSet<>();
        if (object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;
            object = collection.toArray(new Object[0]);
        }

        if (ObjectUtils.isArray(object)) {
            Object[] array = (Object[]) object;
            for (Object o : array) {
                if (o instanceof String) {
                    scope.add((String) o);
                } else {
                    // ignored
                }
            }
        }

        return scope;
    }

}
