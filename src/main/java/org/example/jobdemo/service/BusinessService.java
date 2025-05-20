package org.example.jobdemo.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jobdemo.dto.OpenApiBusinessDto;
import org.example.jobdemo.entity.Business;
import org.example.jobdemo.external.OpenApiClient;
import org.example.jobdemo.repository.BusinessJdbcRepository;
import org.example.jobdemo.repository.BusinessRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final OpenApiClient openApiClient;
    private final BusinessJdbcRepository businessJdbcRepository;

    public List<Business> fetchAndSaveBusinesses() {
        List<OpenApiBusinessDto> dtoList = openApiClient.fetchBusinesses();

        List<Business> businesses = dtoList.stream()
                .map(dto -> Business.builder()
                        .businessName(dto.getBusinessName())
                        .registrationNumber(dto.getRegistrationNumber())
                        .postCode(dto.getPostCode())
                        .roadAddress(dto.getRoadAddress())
                        .industryName(dto.getIndustryName())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        return businessRepository.saveAll(businesses);
    }

    @Transactional
    public void fetchAndSaveAllBusinessesJdbc() {
        List<OpenApiBusinessDto> dtoList = openApiClient.fetchBusinesses();
        businessJdbcRepository.batchInsert(dtoList);
    }

    public Page<Business> searchBusinessesByNameRaw(String keyword, Pageable pageable) {
        return businessRepository.searchByBusinessName(keyword, pageable);
    }

    @Cacheable(value = "businessSearchCache", key = "#keyword + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Map<String, Object> searchBusinessesByNameCached(String keyword, Pageable pageable) {
        log.info("캐시 없이 DB에서 조회 중... (keyword: {}, page: {})", keyword, pageable.getPageNumber());

        Page<Business> page = businessRepository.searchByBusinessName(keyword, pageable);

        Map<String, Object> cachedResult = new HashMap<>();
        cachedResult.put("content", page.getContent()); // List<Business>
        cachedResult.put("page", page.getNumber());
        cachedResult.put("size", page.getSize());
        cachedResult.put("totalElements", page.getTotalElements());
        cachedResult.put("totalPages", page.getTotalPages());
        return cachedResult;
    }

}
