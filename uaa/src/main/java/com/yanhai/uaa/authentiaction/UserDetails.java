package com.yanhai.uaa.authentiaction;

import java.util.Date;
import java.util.Map;

public interface UserDetails extends org.springframework.security.core.userdetails.UserDetails {

    String getId();

    String getEmail();

    String getPhoneNumber();

    Date getCreated();

    Date getModified();

    Long getLastLogonTime();

    Long getPreviousLogonTime();

    Map<String, Object> getAdditionalInformation();
}
