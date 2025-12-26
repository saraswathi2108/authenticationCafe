package com.example.cafe.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import lombok.Data;

@Data
public class BranchResponse {
    private Long id;
    private String branchCode;
    private String branchName;
    private String address;
    private boolean active;
}



