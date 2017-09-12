package com.yanhai.uaa.user.bootstrap;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.yanhai.core.authentication.UserAuthority;
import com.yanhai.uaa.authentiaction.UserDetails;
import com.yanhai.uaa.user.BaseUserDetails;
import com.yanhai.uaa.user.UserServices;
import com.yanhai.uaa.user.exception.UserNotFoundException;

public class UserAdminBootstrap implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(UserAdminBootstrap.class);

    private final UsersProperties usersProperties;

    private final UserServices userServices;

    private final boolean defaultOverride;

    public UserAdminBootstrap(UsersProperties usersProperties, UserServices userServices) {
        this(usersProperties, userServices, true);
    }

    public UserAdminBootstrap(UsersProperties usersProperties, UserServices userServices, boolean defaultOverride) {
        this.usersProperties = usersProperties;
        this.userServices = userServices;
        this.defaultOverride = defaultOverride;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(userServices, "UserServices should not be null");
        this.initUsers();
    }

    private void initUsers() throws Exception {
        Map<String, Map<String, Object>> users = this.usersProperties.getUsers();

        for (Map.Entry<String, Map<String, Object>> entry : users.entrySet()) {
            Map<String, Object> map = entry.getValue();

            String username = entry.getKey();
            String password = String.valueOf(map.get("password"));
            String authorities = String.valueOf(map.get("authorities"));

            if (authorities == null) {
                authorities = UserAuthority.UAA_USER.getUserType();
            }

            BaseUserDetails userDetails = new BaseUserDetails(null, username, password, authorities);
            userDetails.setEmail(String.valueOf(map.get("email")));
            userDetails.setPhoneNumber(String.valueOf(map.get("phoneNumber")));

            Map<String, Object> info = new LinkedHashMap<>(map);
            for (String s : Arrays.asList("password", "authorities", "email", "phoneNumber", "override")) {
                info.remove(s);
            }

            userDetails.setAdditionalInformation(info);

            UserDetails result = null;
            try {
                result = userServices.loadUserByUsername(username);
            } catch (UserNotFoundException e) {
                userServices.addUser(userDetails);
            }

            if (result != null) {
                Boolean override = (Boolean) map.get("override");

                if (override == null) {
                    override = defaultOverride;
                }

                if (override) {
                    userDetails.setId(result.getId());

                    userServices.updateUser(userDetails);

                    log.debug("Override user for: {}", username);
                }
            }

        }
    }

}
