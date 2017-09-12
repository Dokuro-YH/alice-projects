package com.yanhai.uaa.user.endpoints;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yanhai.core.util.PagingUtils;
import com.yanhai.uaa.authentiaction.UserDetails;
import com.yanhai.uaa.user.BaseUserDetails;
import com.yanhai.uaa.user.UserServices;
import com.yanhai.uaa.user.validator.UserAdminValidator;
import com.yanhai.uaa.user.validator.UserValidator;

@RestController
@RequestMapping("/users")
public class UserAdminEndpoints {

    private final UserServices userServices;

    private final UserValidator userValidator;

    public UserAdminEndpoints(UserServices userServices) {
        this.userServices = userServices;
        this.userValidator = new UserAdminValidator(userServices);
    }

    @GetMapping("/{id}")
    public UserDetails get(@PathVariable String id) {
        return userServices.loadUserById(id);
    }

    @GetMapping
    public List<UserDetails> query(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false, defaultValue = "created") String sortBy,
            @RequestParam(required = false, defaultValue = "ascending") String sortOrder,
            @RequestParam(required = false, defaultValue = "1") int startIndex,
            @RequestParam(required = false, defaultValue = "100") int count
    ) {
        List<UserDetails> result = userServices.query(filter, sortBy, sortOrder.equals("ascending"));

        return PagingUtils.subList(result, startIndex, count);
    }

    @PostMapping
    public UserDetails post(@RequestBody BaseUserDetails userDetails) {
        userValidator.validate(userDetails, UserValidator.Mode.CREATE);

        return userServices.addUser(userDetails);
    }

    @PutMapping("/{id}")
    public UserDetails put(@PathVariable String id, @RequestBody BaseUserDetails userDetails) {
        userDetails.setId(id);

        userValidator.validate(userDetails, UserValidator.Mode.MODIFY);

        return userServices.updateUser(userDetails);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        userServices.removeUser(id);
    }
}
