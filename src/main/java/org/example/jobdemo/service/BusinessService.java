package org.example.jobdemo.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jobdemo.dto.OpenApiBusinessDto;
import org.example.jobdemo.entity.Business;
import org.example.jobdemo.external.OpenApiClient;
import org.example.jobdemo.repository.BusinessJdbcRepository;
import org.example.jobdemo.repository.BusinessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;

import javax.sql.DataSource;

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
    public Page<Business> searchBusinessesByNameCached(String keyword, Pageable pageable) {
        log.info("캐시 없이 DB에서 조회 중... (keyword: {}, page: {})", keyword, pageable.getPageNumber());
        return businessRepository.searchByBusinessName(keyword, pageable);
    }
}
