package com.yanhai.hello;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloService {

    private static final Logger log = LoggerFactory.getLogger(HelloApplication.class);

    @GetMapping("/sayHello")
    public Map<String, Object> sayHello(Principal principal) {
        log.info("Principal: {}", principal);

        Map<String, Object> model = new LinkedHashMap<String, Object>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Hello " + principal.getName());
        return model;
    }

}
