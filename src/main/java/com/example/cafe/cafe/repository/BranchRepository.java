package com.example.cafe.cafe.repository;

import com.example.cafe.cafe.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    boolean existsByBranchCode(String branchCode);

    List<Branch> findByActiveTrue();

    List<Branch> findAllByOrderByBranchNameAsc();
}
