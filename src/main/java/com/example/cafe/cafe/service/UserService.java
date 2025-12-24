package com.example.cafe.cafe.service;

import com.example.cafe.cafe.dto.CreateUserRequest;
import com.example.cafe.cafe.dto.CreateUserResponse;
import com.example.cafe.cafe.entity.User;
import com.example.cafe.cafe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.email)) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setEmail(request.email);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole(request.role);
        user.setBranchId(request.branchId);
        user.setFirstLogin(true);
        user.setActive(true);

        User saved = userRepository.save(user);

        CreateUserResponse response = new CreateUserResponse();
        response.id = saved.getId();
        response.email = saved.getEmail();
        response.role = saved.getRole().name();
        response.branchId = saved.getBranchId();
        response.firstLogin = saved.isFirstLogin();
        response.active = saved.isActive();

        return response;
    }
}
