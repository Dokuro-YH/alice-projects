package com.yanhai.uaa.client.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import com.yanhai.core.security.DefaultSecurityContextAccessor;
import com.yanhai.core.security.SecurityContextAccessor;
import com.yanhai.uaa.client.exception.InvalidClientDetailsException;

public class ClientAdminValidator implements ClientDetailsValidator {

    private static final Set<String> VALID_GRANTS = new HashSet<>(Arrays.asList(
            "implicit",
            "password",
            "client_credentials",
            "authorization_code",
            "refresh_token"
    ));

    private static final Set<String> NON_ADMIN_INVALID_GRANTS = new HashSet<>(Arrays.asList("password"));

    private static final Set<String> NON_ADMIN_VALID_AUTHORITIES = new HashSet<>(Arrays.asList("uaa.none"));

    private SecurityContextAccessor sca = new DefaultSecurityContextAccessor();

    public SecurityContextAccessor getSecurityContextAccessor() {
        return sca;
    }

    public void setSecurityContextAccessor(SecurityContextAccessor securityContextAccessor) {
        this.sca = securityContextAccessor;
    }

    @Override
    public ClientDetails validate(ClientDetails clientDetails, Mode mode) {
        BaseClientDetails client = new BaseClientDetails(clientDetails);

        Set<String> requestedGrantTypes = client.getAuthorizedGrantTypes();

        if (requestedGrantTypes.isEmpty()) {
            throw new InvalidClientDetailsException("必须提供至少一个授权类型, 必须是以下其一: " + VALID_GRANTS.toString());
        }

        for (String grant : requestedGrantTypes) {
            if (!VALID_GRANTS.contains(grant)) {
                throw new InvalidClientException(grant + " 是一个不支持的授权类型, 必须是以下其一: " + VALID_GRANTS.toString());
            }
        }

        boolean needRefreshToken = requestedGrantTypes.contains("authorization_code") || requestedGrantTypes.contains("password");

        if (needRefreshToken && !requestedGrantTypes.contains("refresh_token")) {

            requestedGrantTypes.add("refresh_token");
        }

        if (!(sca.isAdmin() || sca.getScopes().contains("clients.admin"))) {

            // 不是管理员，需要严格验证授权类型和作用域
            for (String grant : NON_ADMIN_INVALID_GRANTS) {
                if (requestedGrantTypes.contains(grant)) {
                    throw new InvalidClientDetailsException(grant + " 授权类型需要管理员权限");
                }
            }

            if (requestedGrantTypes.contains("implicit") && requestedGrantTypes.contains("authorization_code")) {
                throw new InvalidClientDetailsException("authorization_code 不能和 implicit 一起使用");
            }

            Set<String> validAuthorities = new HashSet<>(NON_ADMIN_VALID_AUTHORITIES);
            if (requestedGrantTypes.contains("client_credentials")) {
                validAuthorities.add("uaa.resource");
            }

            for (String authority : AuthorityUtils.authorityListToSet(client.getAuthorities())) {
                if (!validAuthorities.contains(authority)) {
                    throw new InvalidClientDetailsException(authority + " 是一个不支持的权限, 必须是以下其一: " + validAuthorities.toString());
                }
            }
        }


        return client;
    }

}
