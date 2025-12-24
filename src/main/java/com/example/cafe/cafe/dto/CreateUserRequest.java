package com.example.cafe.cafe.dto;

import com.example.cafe.cafe.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateUserRequest {

    @Email
    @NotBlank
    public String email;

    @NotBlank
    public String password;

    @NotNull
    public Role role;

    public Long branchId;
}
