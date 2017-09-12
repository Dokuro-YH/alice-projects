package com.yanhai.uaa.user.bootstrap;

import com.yanhai.core.resource.jdbc.DefaultLimitSqlAdapter;
import com.yanhai.core.resource.jdbc.JdbcPagingListFactory;
import com.yanhai.uaa.user.JdbcUserServices;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@JdbcTest
public class UserAdminBootstrapTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UserAdminBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {
        UsersProperties usersProperties = new UsersProperties();
        Map<String, Map<String, Object>> users = new LinkedHashMap<>();
        Map<String, Object> admin = new HashMap<>();
        admin.put("password", "123456");
        admin.put("authorities", "admin,user");
        admin.put("email", "admin@test.org");
        admin.put("phoneNumber", "13888888888");
        users.put("admin", admin);

        Map<String, Object> user = new HashMap<>();
        user.put("password", "654321");
        users.put("user", user);

        usersProperties.setUsers(users);

        JdbcPagingListFactory jdbcPagingListFactory = new JdbcPagingListFactory(jdbcTemplate, new DefaultLimitSqlAdapter());
        JdbcUserServices jdbcUserServices = new JdbcUserServices(jdbcTemplate, jdbcPagingListFactory, NoOpPasswordEncoder.getInstance());

        this.bootstrap = new UserAdminBootstrap(usersProperties, jdbcUserServices);
    }

    @After
    public void tearDown() throws Exception {
        jdbcTemplate.update("DELETE FROM user");
    }

    @Test
    public void afterPropertiesSet() throws Exception {
        this.bootstrap.afterPropertiesSet();

        List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT * FROM user");

        assertEquals(2, users.size());
        assertEquals("admin", users.get(0).get("username"));
        assertEquals("123456", users.get(0).get("password"));
    }

}