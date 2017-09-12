package com.yanhai.core.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BaseUserPrincipal implements UserPrincipal {

    private final String id;

    private final String name;

    @JsonIgnore
    private final Collection<? extends GrantedAuthority> authorities;

    private final Map<String, Object> additionalInformation;

    @JsonProperty("authorities")
    public List<String> getAuthoritiesAsStrings() {
        return new ArrayList<String>(AuthorityUtils.authorityListToSet(authorities));
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalInformation() {
        return additionalInformation;
    }

    public BaseUserPrincipal(String id, String name, String email, String phoneNumber,
                             Collection<? extends GrantedAuthority> authorities,
                             Date created, Date modified, Long lastLogonTime, Long previousLogonTime,
                             Map<String, Object> additionalInformation) {
        this.id = id;
        this.name = name;
        this.authorities = authorities;
        this.additionalInformation = additionalInformation;
        this.additionalInformation.put("email", email);
        this.additionalInformation.put("phoneNumber", phoneNumber);
        this.additionalInformation.put("created", created);
        this.additionalInformation.put("modified", modified);
        this.additionalInformation.put("lastLogonTime", lastLogonTime);
        this.additionalInformation.put("previousLogonTime", previousLogonTime);
    }

    public BaseUserPrincipal(String name) {
        this.id = name;
        this.name = name;
        this.authorities = UserAuthority.NONE_AUTHORITIES;
        this.additionalInformation = new LinkedHashMap<>();
    }

    public BaseUserPrincipal(String id, String name, Collection<? extends GrantedAuthority> authorities, Map<String, Object> additionalInformation) {
        this.id = id;
        this.name = name;
        this.authorities = authorities;
        this.additionalInformation = additionalInformation == null ? new LinkedHashMap<>() : additionalInformation;
    }
}
