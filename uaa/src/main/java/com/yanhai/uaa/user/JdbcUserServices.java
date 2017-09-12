package com.yanhai.uaa.user;

import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import com.yanhai.core.resource.Queryable;
import com.yanhai.core.resource.jdbc.AbstractQueryable;
import com.yanhai.core.resource.jdbc.JdbcPagingListFactory;
import com.yanhai.core.util.JsonUtils;
import com.yanhai.uaa.authentiaction.UserDetails;
import com.yanhai.uaa.user.exception.UserAlreadyExistsException;
import com.yanhai.uaa.user.exception.UserNotFoundException;

public class JdbcUserServices extends AbstractQueryable<UserDetails>
        implements UserServices, Queryable<UserDetails> {

    private static final String TABLE_NAME = "user";

    private static final String ALL_FIELDS = "id,username,password,authorities,email,phone_number,created,modified,last_logon_time,previous_logon_time,additional_information";

    private static final String QUERY_ALL = "select " + ALL_FIELDS + " from " + TABLE_NAME;

    private static final String QUERY_BY_ID = QUERY_ALL + " WHERE id = ?";

    private static final String QUERY_BY_USERNAME = QUERY_ALL + " WHERE username = ?";

    private static final String CREATE_USER = "INSERT INTO user (" + ALL_FIELDS + ") VALUES(?,?,?,?,?,?,?,?,?,?,?)";

    private static final String UPDATE_USER = "UPDATE user SET username=?,password=?,authorities=?,email=?,phone_number=?,additional_information=?,modified=? WHERE id = ?";

    private static final String DELETE_USER = "DELETE FROM user WHERE id = ?";

    private static final RowMapper<UserDetails> rowMapper = new UserRowMapper();

    private final JdbcTemplate jdbcTemplate;

    private final PasswordEncoder encoder;

    public JdbcUserServices(JdbcTemplate jdbcTemplate, JdbcPagingListFactory pagingListFactory,
                            PasswordEncoder passwordEncoder) {
        super(jdbcTemplate, pagingListFactory, rowMapper);
        this.jdbcTemplate = jdbcTemplate;
        this.encoder = passwordEncoder == null ? NoOpPasswordEncoder.getInstance() : passwordEncoder;
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
    public UserDetails loadUserById(String id) throws UserNotFoundException {
        try {
            return jdbcTemplate.queryForObject(QUERY_BY_ID, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("用户不存在: " + id);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        try {
            return jdbcTemplate.queryForObject(QUERY_BY_USERNAME, rowMapper, username);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException("用户不存在: " + username);
        }
    }

    @Override
    public UserDetails addUser(UserDetails userDetails) {
        String id = UUID.randomUUID().toString();
        Timestamp t = new Timestamp(System.currentTimeMillis());

        try {
            jdbcTemplate.update(CREATE_USER, ps -> {
                int pos = 1;
                ps.setString(pos++, id);
                ps.setString(pos++, userDetails.getUsername());
                ps.setString(pos++, encoder.encode(userDetails.getPassword()));
                ps.setString(pos++, StringUtils.collectionToCommaDelimitedString(userDetails.getAuthorities()));
                ps.setString(pos++, userDetails.getEmail());
                ps.setString(pos++, userDetails.getPhoneNumber());
                ps.setTimestamp(pos++, t);
                ps.setTimestamp(pos++, t);
                ps.setLong(pos++, 0);
                ps.setLong(pos++, 0);
                ps.setString(pos++, getAdditionalInformation(userDetails));
            });
        } catch (DuplicateKeyException e) {
            throw new UserAlreadyExistsException("用户已存在: " + userDetails.getUsername());
        }

        return loadUserById(id);
    }

    @Override
    public UserDetails updateUser(UserDetails userDetails) throws UserNotFoundException {
        Timestamp t = new Timestamp(System.currentTimeMillis());

        int update = jdbcTemplate.update(UPDATE_USER, ps -> {
            int pos = 1;

            ps.setString(pos++, userDetails.getUsername());
            ps.setString(pos++, encoder.encode(userDetails.getPassword()));
            ps.setString(pos++, StringUtils.collectionToCommaDelimitedString(userDetails.getAuthorities()));
            ps.setString(pos++, userDetails.getEmail());
            ps.setString(pos++, userDetails.getPhoneNumber());
            ps.setString(pos++, getAdditionalInformation(userDetails));
            ps.setTimestamp(pos++, t);
            ps.setString(pos++, userDetails.getId());
        });

        if (update == 0) {
            throw new UserNotFoundException("用户不存在: " + userDetails.getId());
        }

        return loadUserById(userDetails.getId());
    }

    private String getAdditionalInformation(UserDetails userDetails) {
        try {
            return JsonUtils.writeValueAsString(userDetails.getAdditionalInformation());
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public void removeUser(String id) {
        jdbcTemplate.update(DELETE_USER, id);
    }

}
