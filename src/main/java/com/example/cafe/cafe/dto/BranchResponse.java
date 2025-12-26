package com.example.cafe.cafe.dto;

import lombok.Data;

@Data
public class BranchResponse {

    public Long id;
    public String branchCode;
    public String branchName;
    public boolean active;
}
