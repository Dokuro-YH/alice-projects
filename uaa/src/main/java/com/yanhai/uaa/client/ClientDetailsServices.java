package com.yanhai.uaa.client;

import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.NoSuchClientException;

import com.yanhai.core.resource.Queryable;

public interface ClientDetailsServices extends ClientDetailsService, Queryable<ClientDetails> {

    ClientDetails addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException;

    ClientDetails updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException;

    void updateClientSecret(String clientId, String secret) throws NoSuchClientException;

    void removeClientDetails(String clientId) throws NoSuchClientException;
}
