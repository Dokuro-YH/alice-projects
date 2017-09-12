package com.yanhai.uaa.client.endpoints;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yanhai.core.security.DefaultSecurityContextAccessor;
import com.yanhai.core.security.SecurityContextAccessor;
import com.yanhai.core.util.PagingUtils;
import com.yanhai.uaa.client.ClientDetailsServices;
import com.yanhai.uaa.client.validator.ClientAdminValidator;
import com.yanhai.uaa.client.validator.ClientDetailsValidator;

@RestController
@RequestMapping("/oauth/clients")
public class ClientAdminEndpoints {

    @Autowired
    private ClientDetailsServices clientDetailsServices;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private SecurityContextAccessor securityContextAccessor = new DefaultSecurityContextAccessor();

    private ClientDetailsValidator validator = new ClientAdminValidator();

    @GetMapping("/{clientId}")
    public ClientDetails get(@PathVariable String clientId) {
        return clientDetailsServices.loadClientByClientId(clientId);
    }

    @GetMapping
    public List<ClientDetails> query(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ascending", required = false) String sortOrder,
            @RequestParam(defaultValue = "1", required = false) int startIndex,
            @RequestParam(defaultValue = "100", required = false) int count
    ) {
        List<ClientDetails> result = clientDetailsServices.query(filter, sortBy, sortOrder.equals("ascending"));

        return PagingUtils.subList(result, startIndex, count);
    }

    @PostMapping
    public ClientDetails post(@RequestBody BaseClientDetails clientDetails) {
        validator.validate(clientDetails, ClientDetailsValidator.Mode.CREATE);

        return clientDetailsServices.addClientDetails(clientDetails);
    }

    @PutMapping("/{clientId}")
    public ClientDetails put(@PathVariable String clientId, @RequestBody BaseClientDetails clientDetails) {
        clientDetails.setClientId(clientId);
        validator.validate(clientDetails, ClientDetailsValidator.Mode.MODIFY);

        return clientDetailsServices.updateClientDetails(clientDetails);
    }

    @PutMapping("/{clientId}/secret")
    public void updateSecret(@PathVariable String clientId, @RequestBody SecretChangeRequest change) {

        ClientDetails clientDetails = clientDetailsServices.loadClientByClientId(clientId);

        if (clientDetails == null) {
            throw new NoSuchClientException("没有找到客户端: " + clientId);
        }

        checkPasswordChangeIsAllowed(clientDetails, change.getOldSecret());

        clientDetailsServices.updateClientSecret(clientId, change.getNewSecret());
    }

    @DeleteMapping("/{clientId}")
    public void delete(@PathVariable String clientId) {

        String currentClientId = securityContextAccessor.getClientId();

        if (!securityContextAccessor.isAdmin() && !clientId.equals(currentClientId)) {
            throw new IllegalStateException("错误的请求, 没有权限删除其他的客户端");
        }

        clientDetailsServices.removeClientDetails(clientId);
    }

    private void checkPasswordChangeIsAllowed(ClientDetails clientDetails, String oldScret) {
        boolean matches = passwordEncoder.matches(oldScret, clientDetails.getClientSecret());

        if (!matches) {
            throw new BadCredentialsException("错误的请求, 密码不匹配");
        }

        String currentClientId = securityContextAccessor.getClientId();

        if (!securityContextAccessor.isAdmin() && !clientDetails.getClientId().equals(currentClientId)) {
            throw new IllegalStateException("错误的请求, 没有权限修改其他客户端的密码");
        }
    }
}
