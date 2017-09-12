package com.yanhai.uaa.authentiaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.yanhai.core.authentication.BaseUserPrincipal;
import com.yanhai.core.authentication.UserPrincipal;
import com.yanhai.uaa.user.UserDatabase;

public class AuthzAuthenticationManager implements AuthenticationManager {

    private static final Logger log = LoggerFactory.getLogger(AuthzAuthenticationManager.class);
    private final PasswordEncoder encoder;
    private final UserDatabase userDatabase;

    public AuthzAuthenticationManager(PasswordEncoder passwordEncoder, UserDatabase userDatabase) {
        this.encoder = passwordEncoder;
        this.userDatabase = userDatabase;
    }

    @Override
    public Authentication authenticate(Authentication req) throws AuthenticationException {
        if (req.getCredentials() == null) {
            throw new BadCredentialsException("密码不能为空");
        }

        UserDetails userDetails = getUserDetails(req);

        if (userDetails == null) {
            log.debug("用户未找到: " + req.getName());
        } else {
            boolean passwordMatches = ((CharSequence) req.getCredentials()).length() != 0
                    && encoder.matches((CharSequence) req.getCredentials(), userDetails.getPassword());

            if (!passwordMatches) {
                log.debug("用户密码不匹配 " + req.getName());
            } else {
                userDatabase.updateLastLogonTime(userDetails.getId());

                UserPrincipal userPrincipal = getUserPrincipal(userDetails);

                UsernamePasswordAuthenticationToken success = new UsernamePasswordAuthenticationToken(userPrincipal, req.getCredentials(), userDetails.getAuthorities());

                return success;
            }
        }

        throw new BadCredentialsException("用户名或密码错误");
    }

    private UserDetails getUserDetails(Authentication req) {
        try {
            UserDetails userDetails = userDatabase.retrieveUserByUsername(req.getName());
            if (userDetails != null) {
                return userDetails;
            }
        } catch (UsernameNotFoundException ignored) {
        }
        return null;
    }

    private UserPrincipal getUserPrincipal(UserDetails userDetails) {
        return new BaseUserPrincipal(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getPhoneNumber(),
                userDetails.getAuthorities(),
                userDetails.getCreated(),
                userDetails.getModified(),
                userDetails.getLastLogonTime(),
                userDetails.getPreviousLogonTime(),
                userDetails.getAdditionalInformation()
        );
    }

}
