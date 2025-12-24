package com.example.cafe.cafe.controller;


import com.example.cafe.cafe.dto.ChangePasswordRequest;
import com.example.cafe.cafe.dto.LoginRequest;
import com.example.cafe.cafe.dto.LoginResponse;
import com.example.cafe.cafe.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/change-password")
    public void changePassword(
            @RequestHeader("email") String email,
            @RequestBody ChangePasswordRequest request) {
        authService.changePassword(email, request);
    }
}
