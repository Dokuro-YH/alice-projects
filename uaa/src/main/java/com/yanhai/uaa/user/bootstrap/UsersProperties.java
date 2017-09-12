package com.yanhai.uaa.user.bootstrap;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "uaa")
public class UsersProperties {

    private Map<String, Map<String, Object>> users = new LinkedHashMap<>();

    public Map<String, Map<String, Object>> getUsers() {
        return users;
    }

    public void setUsers(Map<String, Map<String, Object>> users) {
        this.users = users;
    }
}
