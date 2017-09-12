package com.yanhai.uaa.client.bootstrap;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yanhai.uaa.client.ClientDetailsServices;

public class ClientAdminBootstrap implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(ClientAdminBootstrap.class);

    private final ClientsProperties properties;

    private final ClientDetailsServices clientDetailsServices;

    private final boolean defaultOverride;

    public ClientAdminBootstrap(ClientsProperties properties, ClientDetailsServices clientDetailsServices) {
        this(properties, clientDetailsServices, true);
    }

    public ClientAdminBootstrap(ClientsProperties clientsProperties, ClientDetailsServices clientDetailsServices, boolean defaultOverride) {
        this.properties = clientsProperties;
        this.clientDetailsServices = clientDetailsServices;
        this.defaultOverride = defaultOverride;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(clientDetailsServices, "ClientDetailsServices should not be null");
        this.initClients();
    }

    private void initClients() {
        Map<String, Map<String, Object>> clients = properties.getClients();

        for (Map.Entry<String, Map<String, Object>> entry : clients.entrySet()) {
            String clientId = entry.getKey();

            Map<String, Object> map = entry.getValue();


            BaseClientDetails clientDetails = new BaseClientDetails(clientId, (String) map.get("resourceIds"),
                    (String) map.get("scopes"), (String) map.get("grantTypes"), (String) map.get("authorities"),
                    (String) map.get("redirectUris"));

            clientDetails.setClientSecret((String) map.get("secret"));

            String autoApprove = String.valueOf(map.get("autoApprove"));
            if (autoApprove != null) {
                clientDetails.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(autoApprove));
            }

            clientDetails.setAccessTokenValiditySeconds((Integer) map.get("accessTokenValidity"));
            clientDetails.setRefreshTokenValiditySeconds((Integer) map.get("refreshTokenValidity"));

            Map<String, Object> info = new LinkedHashMap<>(map);
            for (String s : Arrays.asList("secret", "resourceIds", "scopes", "autoApprove",
                    "grantTypes", "authorities", "redirectUris", "accessTokenValidity", "refreshTokenValidity")) {
                info.remove(s);
            }

            clientDetails.setAdditionalInformation(info);

            try {
                clientDetailsServices.addClientDetails(clientDetails);
            } catch (ClientAlreadyExistsException e) {

                Boolean override = (Boolean) map.get("override");

                if (override == null) {
                    override = defaultOverride;
                }

                if (override) {
                    clientDetailsServices.updateClientDetails(clientDetails);

                    log.debug("Override client details for: {}", clientId);
                }
            }

        }
    }
}
