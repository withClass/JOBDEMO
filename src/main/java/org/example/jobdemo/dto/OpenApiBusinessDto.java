package org.example.jobdemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OpenApiBusinessDto {

    @JsonProperty("사업장명")
    private String businessName;

    @JsonProperty("사업자등록번호")
    private String registrationNumber;

    @JsonProperty("우편번호")
    private String postCode;

    @JsonProperty("사업장도로명상세주소")
    private String roadAddress;

    @JsonProperty("사업장업종코드명")
    private String industryName;

    // 🔽 추가: 월별 지표 및 상태 관련 항목
    @JsonProperty("자료생성년월")
    private String reportMonth; // 예: "2023-12"

    @JsonProperty("신규취득자수")
    private int newMembers;

    @JsonProperty("상실가입자수")
    private int resignedMembers;

    @JsonProperty("당월고지금액")
    private BigDecimal billingAmount;

    @JsonProperty("사업장가입상태코드 1 등록 2 탈퇴")
    private String statusCode; // 문자열로 받는 게 안전 (예: "1", "2")
}
