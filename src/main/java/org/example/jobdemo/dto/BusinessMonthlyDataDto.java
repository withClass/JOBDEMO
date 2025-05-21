package org.example.jobdemo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BusinessMonthlyDataDto {
    private Long businessId;
    private String reportMonth;
    private int newMembers;
    private int resignedMembers;
    private BigDecimal billingAmount;
    private String statusCode;
}
