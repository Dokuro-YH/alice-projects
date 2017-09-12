package com.yanhai.uaa.user;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.yanhai.uaa.authentiaction.UserDetails;

public class JdbcUserDatabase implements UserDatabase {

    private static final String QUERY_FIELDS = "id,username,password,authorities,email,phone_number,created,modified,last_logon_time,previous_logon_time,additional_information";

    private static final String QUERY_ALL = "select " + QUERY_FIELDS + " from user";

    private static final String RETRIEVE_USER_BY_ID = QUERY_ALL + " WHERE id = ?";

    private static final String RETRIEVE_USER_BY_EMAIL = QUERY_ALL + " WHERE email = ?";

    private static final String RETRIEVE_USER_BY_USERNAME = QUERY_ALL + " WHERE username = ?";

    private static final String RETRIEVE_USER_BY_PHONE_NUMBER = QUERY_ALL + " WHERE phone_number = ?";

    private static final String UPDATE_LAST_LOGON_TIME = "UPDATE user SET previous_logon_time = last_logon_time, last_logon_time = ? WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<UserDetails> rowMapper = new UserRowMapper();

    public JdbcUserDatabase(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails retrieveUserById(String id) throws UsernameNotFoundException {
        try {
            return jdbcTemplate.queryForObject(RETRIEVE_USER_BY_ID, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("用户不存在: " + id, e);
        }
    }

    public UserDetails retrieveUserByEmail(String email) throws UsernameNotFoundException {
        try {
            return jdbcTemplate.queryForObject(RETRIEVE_USER_BY_EMAIL, rowMapper, email);
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("用户不存在: " + email, e);
        }
    }

    @Override
    public UserDetails retrieveUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return jdbcTemplate.queryForObject(RETRIEVE_USER_BY_USERNAME, rowMapper, username);
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("用户不存在: " + username, e);
        }
    }

    @Override
    public UserDetails retrieveUserByPhoneNumber(String phoneNumber) throws UsernameNotFoundException {
        try {
            return jdbcTemplate.queryForObject(RETRIEVE_USER_BY_PHONE_NUMBER, rowMapper, phoneNumber);
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("用户不存在: " + phoneNumber, e);
        }
    }

    @Override
    public void updateLastLogonTime(String id) {
        jdbcTemplate.update(UPDATE_LAST_LOGON_TIME, System.currentTimeMillis(), id);
    }

}
