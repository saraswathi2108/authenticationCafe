package com.example.cafe.cafe.repository;

import com.example.cafe.cafe.entity.Branch;
import com.example.cafe.cafe.entity.Role;
import com.example.cafe.cafe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    Optional<User> findByEmail(String email);

}
