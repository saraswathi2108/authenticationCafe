package com.example.cafe.cafe.controller;

import com.example.cafe.cafe.dto.CreateUserRequest;
import com.example.cafe.cafe.dto.CreateUserResponse;
import com.example.cafe.cafe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PostMapping("/admin/users")
    public CreateUserResponse createUser(
            @RequestBody CreateUserRequest request
    ) {
        return userService.createUser(request);
    }
}
