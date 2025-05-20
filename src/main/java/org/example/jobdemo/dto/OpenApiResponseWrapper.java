package org.example.jobdemo.dto;

import lombok.Data;
import java.util.List;

@Data
public class OpenApiResponseWrapper {
    private int totalCount;
    private List<OpenApiBusinessDto> data;
}