package org.example.jobdemo.dto;

import lombok.Data;
import java.util.List;

@Data
public class OpenApiResponseWrapper {
    private List<OpenApiBusinessDto> data;
}