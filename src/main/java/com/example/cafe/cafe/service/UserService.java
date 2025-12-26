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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserResponse createUser(CreateUserRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().isEmpty()) {
            throw new RuntimeException("Unauthorized");
        }

        String currentRole = auth.getAuthorities().iterator().next().getAuthority();

        User creator = userRepository.findByEmail(auth.getName().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        if (currentRole.equals("ROLE_STAFF")) {
            throw new RuntimeException("Staff cannot create users");
        }

        if (request.role == Role.ADMIN && !currentRole.equals("ROLE_ADMIN")) {
            throw new RuntimeException("Only admin can create admin");
        }

        if (currentRole.equals("ROLE_MANAGER") && request.role == Role.MANAGER) {
            throw new RuntimeException("Manager cannot create manager");
        }

        if (currentRole.equals("ROLE_MANAGER") && request.role != Role.STAFF) {
            throw new RuntimeException("Manager can create only staff");
        }

        if (userRepository.existsByEmail(request.email.toLowerCase())) {
            throw new UserAlreadyExistsException("User already exists with email: " + request.email);
        }

        Branch branch = null;

        if (request.role != Role.ADMIN) {
            if (currentRole.equals("ROLE_MANAGER")) {
                branch = creator.getBranch();
                if (branch == null) {
                    throw new RuntimeException("Manager has no branch assigned");
                }
            } else {
                branch = branchRepository.findById(request.branchId)
                        .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
                if (!branch.isActive()) {
                    throw new RuntimeException("Branch is inactive");
                }
            }
        }

        User user = new User();
        user.setEmail(request.email.toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole(request.role);
        user.setBranch(branch);
        user.setFirstLogin(true);
        user.setActive(true);

        return mapToResponse(userRepository.save(user));
    }

    public List<CreateUserResponse> getAllManagers() {
        return userRepository.findByRole(Role.MANAGER)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<CreateUserResponse> getStaff() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new RuntimeException("Unauthorized");
        }

        User currentUser = userRepository.findByEmail(auth.getName().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> staff;

        if (currentUser.getRole() == Role.ADMIN) {
            staff = userRepository.findByRole(Role.STAFF);
        } else if (currentUser.getRole() == Role.MANAGER) {
            if (currentUser.getBranch() == null) {
                throw new RuntimeException("Manager has no branch assigned");
            }
            staff = userRepository.findByRoleAndBranchId(
                    Role.STAFF,
                    currentUser.getBranch().getId()
            );
        } else {
            throw new RuntimeException("Access denied");
        }

        return staff.stream().map(this::mapToResponse).toList();
    }

    public List<CreateUserResponse> getStaffByBranchId(Long branchId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.getRole() == Role.MANAGER) {
            if (currentUser.getBranch() == null ||
                    !currentUser.getBranch().getId().equals(branchId)) {
                throw new RuntimeException("Access denied");
            }
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        return userRepository.findByRoleAndBranchId(Role.STAFF, branch.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<CreateUserResponse> getManagersByBranchId(Long branchId) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        return userRepository.findByRoleAndBranchId(Role.MANAGER, branch.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CreateUserResponse updateUserStatus(Long userId, boolean active) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new RuntimeException("Unauthorized");
        }

        User currentUser = userRepository.findByEmail(auth.getName().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (targetUser.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admin status cannot be changed");
        }

        if (currentUser.getRole() == Role.MANAGER) {
            if (targetUser.getRole() != Role.STAFF) {
                throw new RuntimeException("Manager can update only staff");
            }
            if (currentUser.getBranch() == null ||
                    targetUser.getBranch() == null ||
                    !currentUser.getBranch().getId()
                            .equals(targetUser.getBranch().getId())) {
                throw new RuntimeException("Access denied");
            }
        }

        targetUser.setActive(active);
        userRepository.save(targetUser);

        return mapToResponse(targetUser);
    }

    private CreateUserResponse mapToResponse(User user) {
        CreateUserResponse response = new CreateUserResponse();
        response.id = user.getId();
        response.email = user.getEmail();
        response.role = user.getRole().name();
        response.branchId = user.getBranch() != null ? user.getBranch().getId() : null;
        response.firstLogin = user.isFirstLogin();
        response.active = user.isActive();
        return response;
    }


}
