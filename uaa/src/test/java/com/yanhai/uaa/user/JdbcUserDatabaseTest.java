package com.yanhai.uaa.user;

import com.yanhai.uaa.authentiaction.UserDetails;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@JdbcTest
public class JdbcUserDatabaseTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private JdbcUserDatabase database;

    @Before
    public void setUp() throws Exception {
        this.database = new JdbcUserDatabase(jdbcTemplate);

        jdbcTemplate.execute("INSERT INTO user(id, username, password, email, phone_number, authorities, last_logon_time) " +
                "VALUES('1', 'admin', 'admin', 'admin@test.org', '123', 'uaa.admin', 1000), " +
                "('2', 'user', 'user', 'user@test.org', '456', 'uaa.user', 1000)");
    }

    @After
    public void tearDown() throws Exception {
        jdbcTemplate.execute("DELETE FROM user");
    }

    @Test
    public void retrieveUserById() throws Exception {
        UserDetails userDetails = this.database.retrieveUserById("2");
        assertNotNull(userDetails);
        assertEquals("user@test.org", userDetails.getEmail());
    }

    @Test
    public void retrieveUserByEmail() throws Exception {
        UserDetails userDetails = this.database.retrieveUserByEmail("user@test.org");
        assertNotNull(userDetails);
        assertEquals("user@test.org", userDetails.getEmail());
    }

    @Test
    public void retrieveUserByUsername() throws Exception {
        UserDetails userDetails = this.database.retrieveUserByUsername("admin");
        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
    }

    @Test
    public void retrieveUserByPhoneNumber() throws Exception {
        UserDetails userDetails = this.database.retrieveUserByPhoneNumber("123");
        assertNotNull(userDetails);
        assertEquals("123", userDetails.getPhoneNumber());
    }

    @Test
    public void updateLastLogonTime() throws Exception {
        Long currentLastLogonTime = 1000L;

        this.database.updateLastLogonTime("1");

        UserDetails admin = this.database.retrieveUserById("1");

        assertNotEquals(currentLastLogonTime, admin.getLastLogonTime());
        assertEquals(currentLastLogonTime, admin.getPreviousLogonTime());
    }

}