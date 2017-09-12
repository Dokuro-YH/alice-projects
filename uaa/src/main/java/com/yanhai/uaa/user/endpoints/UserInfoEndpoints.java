package com.yanhai.uaa.user.endpoints;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yanhai.core.authentication.BaseUserPrincipal;
import com.yanhai.core.authentication.UserPrincipal;

@RestController
@RequestMapping("/me")
public class UserInfoEndpoints {

    @RequestMapping
    public UserPrincipal get() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();

        if (!(a instanceof OAuth2Authentication)) {
            return null;
        }

        OAuth2Authentication oa = (OAuth2Authentication) a;

        UserPrincipal principal = null;

        if (oa.getPrincipal() instanceof UserPrincipal) {
            principal = (UserPrincipal) oa.getPrincipal();
        }

        if (oa.getPrincipal() instanceof String) {
            principal = new BaseUserPrincipal((String) oa.getPrincipal());
        }

        if (principal != null) {
            OAuth2Request oAuth2Request = oa.getOAuth2Request();
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("scope", oAuth2Request.getScope());
            map.put("resourceIds", oAuth2Request.getResourceIds());
            map.put("authorities", oAuth2Request.getAuthorities());

            principal.getAdditionalInformation().put("oauth2Request", map);
        }

        return principal;
    }


}
