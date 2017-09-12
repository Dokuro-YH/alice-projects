package com.yanhai.uaa.client.endpoints;

import lombok.Data;

@Data
public class SecretChangeRequest {

    private String oldSecret;

    private String newSecret;

}
