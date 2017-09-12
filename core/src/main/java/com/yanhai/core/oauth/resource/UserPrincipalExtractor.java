package com.yanhai.core.oauth.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.ObjectUtils;

import com.yanhai.core.authentication.BaseUserPrincipal;
import com.yanhai.core.authentication.UserAuthority;

public class UserPrincipalExtractor implements PrincipalExtractor {

    private static final String[] PUBLIC_KEYS = new String[]{"id", "userid", "user_id"};

    private static final String[] NAME_KEYS = new String[]{"username", "user", "login", "name"};

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        String extractPublicKey = extractPublicKey(map);
        String extractName = extractName(map);

        if (extractPublicKey == null) {
            return null;
        }

        String id = extractPublicKey;
        String name = extractName == null ? "unknown" : extractName;
        List<GrantedAuthority> grantedAuthorities = asAuthorities(map.get("authorities"));

        Map<String, Object> info = new LinkedHashMap<>(map);

        List<String> ignoredInfoKeys = new LinkedList<>();
        ignoredInfoKeys.addAll(Arrays.asList(PUBLIC_KEYS));
        ignoredInfoKeys.addAll(Arrays.asList(NAME_KEYS));
        ignoredInfoKeys.add("authorities");

        for (String s : ignoredInfoKeys) {
            info.remove(s);
        }

        return new BaseUserPrincipal(id, name, grantedAuthorities, info);
    }

    public String extractPublicKey(Map<String, Object> map) {
        for (String key : PUBLIC_KEYS) {
            if (map.containsKey(key)) {
                return asString(map.get(key));
            }
        }
        return extractName(map);
    }

    public String extractName(Map<String, Object> map) {
        for (String key : NAME_KEYS) {
            if (map.containsKey(key)) {
                return asString(map.get(key));
            }
        }
        return null;
    }

    private String asString(Object object) {
        if (object instanceof String) {
            return (String) object;
        }

        if (object != null) {
            return object.toString();
        }

        return null;
    }

    private List<GrantedAuthority> asAuthorities(Object object) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;
            object = collection.toArray(new Object[0]);
        }

        if (ObjectUtils.isArray(object)) {
            Object[] array = (Object[]) object;
            for (Object o : array) {
                if (o instanceof String) {
                    authorities.add(new SimpleGrantedAuthority((String) o));
                }
            }
        }

        if (authorities.isEmpty()) {
            authorities.add(UserAuthority.UAA_NONE);
        }

        return authorities;
    }
}
