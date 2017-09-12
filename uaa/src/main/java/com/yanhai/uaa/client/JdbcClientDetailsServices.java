package com.yanhai.uaa.client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yanhai.core.resource.jdbc.AbstractQueryable;
import com.yanhai.core.resource.jdbc.JdbcPagingListFactory;
import com.yanhai.core.util.JsonUtils;

public class JdbcClientDetailsServices extends AbstractQueryable<ClientDetails> implements ClientDetailsServices {

    private static final Logger log = LoggerFactory.getLogger(JdbcClientDetailsServices.class);

    private static final String TABLE_NAME = "oauth_client_details";

    private static final String UPDATE_FIELDS = "authorities,authorized_grant_types,resource_ids,scopes,auto_approve_scopes,redirect_uris,access_token_validity,refresh_token_validity,additional_information";

    private static final String ALL_FIELDS = "client_secret," + UPDATE_FIELDS + ",client_id";

    private static final String QUERY_ALL = "SELECT " + ALL_FIELDS + " FROM oauth_client_details";

    private static final String QUERY_BY_ID = QUERY_ALL + " WHERE client_id = ?";

    private static final String ADD_CLIENT = "INSERT INTO oauth_client_details(" + ALL_FIELDS + ") VALUES(?,?,?,?,?,?,?,?,?,?,?)";

    private static final String UPDATE_CLIENT = "UPDATE oauth_client_details SET " + UPDATE_FIELDS.replace(",", "=?,") + "=? where client_id = ?";

    private static final String UPDATE_CLIENT_SECRET = "UPDATE oauth_client_details SET client_secret = ? WHERE client_id = ?";

    private static final String REMOVE_CLIENT = "DELETE FROM oauth_client_details WHERE client_id = ?";

    private static final RowMapper<ClientDetails> rowMapper = new ClientDetailsMapper();

    private final JdbcTemplate jdbcTemplate;

    private final PasswordEncoder passwordEncoder;

    public JdbcClientDetailsServices(JdbcTemplate jdbcTemplate, JdbcPagingListFactory pagingListFactory, PasswordEncoder passwordEncoder) {
        super(jdbcTemplate, pagingListFactory, rowMapper);
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder == null ? NoOpPasswordEncoder.getInstance() : passwordEncoder;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getBaseSqlQuery() {
        return QUERY_ALL;
    }

    @Override
    protected void validateOrderBy(String orderBy) throws IllegalArgumentException {
        super.validateOrderBy(orderBy, ALL_FIELDS);
    }

    @Override
    public ClientDetails addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        try {

            jdbcTemplate.update(ADD_CLIENT, getInsertFields(clientDetails));
        } catch (DuplicateKeyException e) {
            throw new ClientAlreadyExistsException("Client already exists: " + clientDetails.getClientId());
        }

        return loadClientByClientId(clientDetails.getClientId());
    }

    @Override
    public ClientDetails updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        int update = jdbcTemplate.update(UPDATE_CLIENT, getUpdateFields(clientDetails));

        if (update == 0) {
            throw new NoSuchClientException("No such client with clientId: " + clientDetails.getClientId());
        }
        return null;
    }

    @Override
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        int update = jdbcTemplate.update(UPDATE_CLIENT_SECRET, passwordEncoder.encode(secret), clientId);
        if (update == 0) {
            throw new NoSuchClientException("No such client with clientId: " + clientId);
        }
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        jdbcTemplate.update(REMOVE_CLIENT, clientId);
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws NoSuchClientException {
        try {
            return jdbcTemplate.queryForObject(QUERY_BY_ID, rowMapper, clientId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchClientException("No such client with clientId: " + clientId);
        }
    }

    private Object[] getInsertFields(ClientDetails clientDetails) {
        Object[] updateFields = getUpdateFields(clientDetails);
        Object[] insertFields = new Object[updateFields.length + 1];
        System.arraycopy(updateFields, 0, insertFields, 1, updateFields.length);

        insertFields[0] = clientDetails.getClientSecret() == null ? null : passwordEncoder.encode(clientDetails.getClientSecret());
        return insertFields;
    }

    private Object[] getUpdateFields(ClientDetails clientDetails) {
        String json = null;
        try {
            json = JsonUtils.writeValueAsString(clientDetails.getAdditionalInformation());
        } catch (Exception e) {
            log.warn("Could not encode JSON for additional information");
        }
        String autoApproveScopes = getAutoApproveScopes(clientDetails);

        return new Object[]{
                clientDetails.getAuthorities() == null ? null : StringUtils.collectionToCommaDelimitedString(clientDetails.getAuthorities()),
                clientDetails.getAuthorizedGrantTypes() == null ? null : StringUtils.collectionToCommaDelimitedString(clientDetails.getAuthorizedGrantTypes()),
                clientDetails.getResourceIds() == null ? null : StringUtils.collectionToCommaDelimitedString(clientDetails.getResourceIds()),
                clientDetails.getScope() == null ? null : StringUtils.collectionToCommaDelimitedString(clientDetails.getScope()),
                autoApproveScopes,
                clientDetails.getRegisteredRedirectUri() == null ? null : StringUtils.collectionToCommaDelimitedString(clientDetails.getRegisteredRedirectUri()),
                clientDetails.getAccessTokenValiditySeconds(),
                clientDetails.getRefreshTokenValiditySeconds(),
                json,
                clientDetails.getClientId()
        };
    }

    private String getAutoApproveScopes(ClientDetails clientDetails) {
        if (clientDetails.isAutoApprove("true")) {
            return "true";
        }

        Set<String> scopes = new HashSet<>();
        for (String scope : clientDetails.getScope()) {
            if (clientDetails.isAutoApprove(scope)) {
                scopes.add(scope);
            }
        }

        if (scopes.size() == 0) {
            return null;
        }

        return StringUtils.collectionToCommaDelimitedString(scopes);
    }

    private static class ClientDetailsMapper implements RowMapper<ClientDetails> {

        @Override
        public ClientDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            BaseClientDetails clientDetails = new BaseClientDetails();
            clientDetails.setClientId(rs.getString("client_id"));
            clientDetails.setClientSecret(rs.getString("client_secret"));
            clientDetails.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(rs.getString("authorities")));
            clientDetails.setScope(StringUtils.commaDelimitedListToSet(rs.getString("scopes")));
            clientDetails.setResourceIds(StringUtils.commaDelimitedListToSet(rs.getString("resource_ids")));
            clientDetails.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(rs.getString("auto_approve_scopes")));
            clientDetails.setAuthorizedGrantTypes(StringUtils.commaDelimitedListToSet(rs.getString("authorized_grant_types")));
            clientDetails.setRegisteredRedirectUri(StringUtils.commaDelimitedListToSet(rs.getString("redirect_uris")));

            clientDetails.setAccessTokenValiditySeconds(rs.getInt("access_token_validity"));
            if (rs.wasNull()) {
                clientDetails.setAccessTokenValiditySeconds(null);
            }

            clientDetails.setRefreshTokenValiditySeconds(rs.getInt("refresh_token_validity"));
            if (rs.wasNull()) {
                clientDetails.setRefreshTokenValiditySeconds(null);
            }

            String json = rs.getString("additional_information");
            if (json != null)
                try {
                    Map<String, Object> additionalInformation = JsonUtils.readValue(json, new TypeReference<Map<String, Object>>() {
                    });
                    clientDetails.setAdditionalInformation(additionalInformation);
                } catch (Exception e) {
                    log.warn("Could not decode JSON for additional information: ", e);
                }

            return clientDetails;
        }
    }
}
