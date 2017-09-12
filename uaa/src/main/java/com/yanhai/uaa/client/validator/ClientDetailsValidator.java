package com.yanhai.uaa.client.validator;

import org.springframework.security.oauth2.provider.ClientDetails;

public interface ClientDetailsValidator {

    ClientDetails validate(ClientDetails clientDetails, Mode mode);

    enum Mode {
        CREATE, MODIFY
    }
}
