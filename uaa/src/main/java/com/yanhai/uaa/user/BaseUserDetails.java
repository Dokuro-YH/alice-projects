package com.yanhai.uaa.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yanhai.uaa.authentiaction.UserDetails;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@SuppressWarnings("serial")
public class BaseUserDetails implements UserDetails {

    private String id;

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String email;

    private String phoneNumber;

    @JsonIgnore
    private Collection<? extends GrantedAuthority> authorities;

    private Date created;

    private Date modified;

    private boolean enabled;

    private boolean accountNonExpired;

    private boolean credentialsNonExpired;

    private boolean accountNonLocked;

    private Long lastLogonTime;

    private Long previousLogonTime;

    @JsonIgnore
    private Map<String, Object> additionalInformation;

    public BaseUserDetails() {
    }

    public BaseUserDetails(UserDetails prototype) {
        this(
                prototype.getId(),
                prototype.getUsername(),
                prototype.getPassword(),
                prototype.getEmail(),
                prototype.getPhoneNumber(),
                prototype.getAuthorities(),
                prototype.getCreated(),
                prototype.getModified(),
                prototype.isEnabled(),
                prototype.isAccountNonExpired(),
                prototype.isCredentialsNonExpired(),
                prototype.isAccountNonLocked(),
                prototype.getLastLogonTime(),
                prototype.getPreviousLogonTime(),
                prototype.getAdditionalInformation()
        );
    }

    public BaseUserDetails(String id, String username, String password, String authorityString) {
        this(id, username, password, AuthorityUtils.commaSeparatedStringToAuthorityList(authorityString));
    }

    public BaseUserDetails(String id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this(id, username, password, null, null, authorities, new Date(), new Date(), true, true, true, true, null, null, null);
    }

    public BaseUserDetails(String id, String username, String password, String email, String phoneNumber, Collection<? extends GrantedAuthority> authorities, Date created, Date modified, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Long lastLogonTime, Long previousLogonTime, Map<String, Object> additionalInformation) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.authorities = authorities;
        this.created = created;
        this.modified = modified;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.lastLogonTime = lastLogonTime;
        this.previousLogonTime = previousLogonTime;
        this.additionalInformation = additionalInformation;
    }

    @JsonProperty("authorities")
    public List<String> getAuthoritiesAsStrings() {
        return new ArrayList<String>(AuthorityUtils.authorityListToSet(authorities));
    }

    @JsonProperty("authorities")
    public void setAuthoritiesAsStrings(Set<String> values) {
        setAuthorities(AuthorityUtils.createAuthorityList(values.toArray(new String[values.size()])));
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalInformation() {
        return additionalInformation;
    }

    @JsonAnySetter
    public void setAdditionalInformation(Map<String, Object> additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @Override
    public String toString() {
        return "BaseUserDetails [username=" + username + ", password=[PROTECTED], email=" + email
                + ", phoneNumber=" + phoneNumber + ", authorities=" + authorities + ", created=" + created + ", modified="
                + modified + "]";
    }

}
