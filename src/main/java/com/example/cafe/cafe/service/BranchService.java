package com.example.cafe.cafe.service;

import com.example.cafe.cafe.dto.BranchResponse;
import com.example.cafe.cafe.dto.CreateBranchRequest;
import com.example.cafe.cafe.entity.Branch;
import com.example.cafe.cafe.exceptions.ResourceNotFoundException;
import com.example.cafe.cafe.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BranchService {

    private final BranchRepository branchRepository;

    public BranchResponse createBranch(CreateBranchRequest request) {

        if (branchRepository.existsByBranchCode(request.branchCode)) {
            throw new IllegalStateException("Branch code already exists");
        }

        Branch branch = new Branch();
        branch.setBranchCode(request.branchCode);
        branch.setBranchName(request.branchName);
        branch.setAddress(request.address);
        branch.setActive(true);

        return toResponse(branchRepository.save(branch));
    }

    public BranchResponse updateBranchStatus(Long branchId, boolean active) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found with id: " + branchId)
                );

        branch.setActive(active);
        return toResponse(branch);
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> getAllActiveBranches() {

        return branchRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> getAllBranchesForAdmin() {

        return branchRepository.findAllByOrderByBranchNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private BranchResponse toResponse(Branch branch) {
        BranchResponse dto = new BranchResponse();
        dto.id = branch.getId();
        dto.branchCode = branch.getBranchCode();
        dto.branchName = branch.getBranchName();
        dto.active = branch.isActive();
        return dto;
    }
}
