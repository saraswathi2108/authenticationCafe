package com.example.cafe.cafe.service;

import com.example.cafe.cafe.dto.ChangePasswordRequest;
import com.example.cafe.cafe.dto.LoginRequest;
import com.example.cafe.cafe.dto.LoginResponse;
import com.example.cafe.cafe.entity.User;
import com.example.cafe.cafe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.isActive())
            throw new RuntimeException("User disabled");

        if (!passwordEncoder.matches(request.password, user.getPassword()))
            throw new RuntimeException("Invalid credentials");

        LoginResponse response = new LoginResponse();
        response.accessToken = jwtService.generateToken(user);
        response.role = user.getRole().name();
        response.firstLogin = user.isFirstLogin();

        return response;
    }

    public void changePassword(String email, ChangePasswordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.oldPassword, user.getPassword()))
            throw new RuntimeException("Old password incorrect");

        user.setPassword(passwordEncoder.encode(request.newPassword));
        user.setFirstLogin(false);

        userRepository.save(user);
    }
}
