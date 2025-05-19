package org.example.jobdemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OpenApiBusinessDto {

    @JsonProperty("사업장명 WKPL_NM\tVARCHAR(100)")
    private String businessName;

    @JsonProperty("사업자등록번호 BZOWR_RGST_NO VARCHAR(10)")
    private String registrationNumber;

    @JsonProperty("우편번호 ZIP\tVARCHAR(6)")
    private String postCode;

    @JsonProperty("사업장도로명상세주소 WKPL_ROAD_NM_DTL_ADDR VARCHAR(300)")
    private String roadAddress;

    @JsonProperty("사업장업종코드명 VLDT_VL_KRN_NM VARCHAR(200)")
    private String industryName;
}
