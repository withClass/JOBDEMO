package org.example.jobdemo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BusinessDetailDto {
    private Long id;
    private String businessName;
    private String registrationNumber;
    private String postCode;
    private String roadAddress;
    private String industryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<BusinessMonthlyDataDto> monthlyDataList;
}