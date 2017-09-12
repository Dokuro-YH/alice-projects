package com.yanhai.uaa.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yanhai.core.util.JsonUtils;
import com.yanhai.uaa.authentiaction.UserDetails;

class UserRowMapper implements RowMapper<UserDetails> {

    @Override
    public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        String id = rs.getString("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String authorities = rs.getString("authorities");
        BaseUserDetails userDetails = new BaseUserDetails(id, username, password, authorities);

        userDetails.setEmail(rs.getString("email"));
        userDetails.setPhoneNumber(rs.getString("phone_number"));
        userDetails.setCreated(rs.getTimestamp("created"));
        userDetails.setModified(rs.getTimestamp("modified"));

        userDetails.setLastLogonTime(rs.getLong("last_logon_time"));
        userDetails.setPreviousLogonTime(rs.getLong("previous_logon_time"));

        try {
            Map<String, Object> additionalInformation = JsonUtils.readValue(rs.getString("additional_information"), new TypeReference<Map<String, Object>>() {
            });
            userDetails.setAdditionalInformation(additionalInformation);
        } catch (Exception ignored) {
        }

        return userDetails;
    }

}