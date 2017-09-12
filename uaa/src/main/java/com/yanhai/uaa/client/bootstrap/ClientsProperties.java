package com.yanhai.uaa.client.bootstrap;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "uaa")
public class ClientsProperties {

    private Map<String, Map<String, Object>> clients = new HashMap<>();

    public Map<String, Map<String, Object>> getClients() {
        return clients;
    }

    public void setClients(Map<String, Map<String, Object>> clients) {
        this.clients = clients;
    }
}
