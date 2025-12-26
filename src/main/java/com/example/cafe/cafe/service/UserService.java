package com.example.cafe.cafe.service;

import com.example.cafe.cafe.dto.CreateUserRequest;
import com.example.cafe.cafe.dto.CreateUserResponse;
import com.example.cafe.cafe.entity.Branch;
import com.example.cafe.cafe.entity.Role;
import com.example.cafe.cafe.entity.User;
import com.example.cafe.cafe.exceptions.ResourceNotFoundException;
import com.example.cafe.cafe.exceptions.UserAlreadyExistsException;
import com.example.cafe.cafe.repository.BranchRepository;
import com.example.cafe.cafe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserResponse createUser(CreateUserRequest request) {

        if (request.role == Role.ADMIN) {
            throw new IllegalStateException("Admin creation not allowed");
        }

        if (userRepository.existsByEmail(request.email)) {
            throw new UserAlreadyExistsException(
                    "User already exists with email: " + request.email
            );
        }

        Branch branch = null;

        if (request.role != Role.ADMIN) {
            branch = branchRepository.findById(request.branchId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Branch not found")
                    );

            if (!branch.isActive()) {
                throw new RuntimeException("Branch is inactive");
            }
        }

        User user = new User();
        user.setEmail(request.email);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole(request.role);
        user.setBranch(branch);
        user.setFirstLogin(true);
        user.setActive(true);

        User saved = userRepository.save(user);

        CreateUserResponse response = new CreateUserResponse();
        response.id = saved.getId();
        response.email = saved.getEmail();
        response.role = saved.getRole().name();
        response.branchId = saved.getBranch() != null ? saved.getBranch().getId() : null;
        response.firstLogin = saved.isFirstLogin();
        response.active = saved.isActive();

        return response;
    }
}
