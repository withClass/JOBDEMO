package org.example.jobdemo.controller;

import lombok.RequiredArgsConstructor;
import org.example.jobdemo.dto.BusinessDetailDto;
import org.example.jobdemo.dto.BusinessDto;
import org.example.jobdemo.dto.BusinessMonthlyDataDto;
import org.example.jobdemo.dto.Popular;
import org.example.jobdemo.entity.Business;
import org.example.jobdemo.service.BusinessService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;
    private final StringRedisTemplate redisTemplate;

    // 외부 API에서 가져와 DB에 저장하는 엔드포인트
    @PostMapping("/import")
    public ResponseEntity<List<Business>> importBusinessesFromApi() {
        List<Business> result = businessService.fetchAndSaveBusinesses();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/import/jdbc")
    public void importBusinessesFromApiBatch() {
        businessService.fetchAndSaveAllBusinessesJdbc();
    }

    @GetMapping("/v1/search")
    public ResponseEntity<Page<Business>> searchBusinessesRaw(
            @RequestParam("keyword") String keyword,
            Pageable pageable) {
        Page<Business> result = businessService.searchBusinessesByNameRaw(keyword, pageable);
        return ResponseEntity.ok(result);
    }

//    @GetMapping("/v2/search")
//    public ResponseEntity<Page<Business>> searchBusinessesCached(
//            @RequestParam("keyword") String keyword,
//            Pageable pageable) {
//        Page<Business> result = businessService.searchBusinessesByNameCached(keyword, pageable);
//        return ResponseEntity.ok(result);
//    }

    @GetMapping("/v2/search")
    public ResponseEntity<Page<BusinessDto>> searchBusinessesCached(
            @RequestParam("keyword") String keyword,
            Pageable pageable) {

        redisTemplate.opsForZSet().incrementScore("popular:search", keyword, 1);

        Map<String, Object> cachedData = businessService.searchBusinessesByNameCached(keyword, pageable);

        // 캐시된 데이터에서 Page<Business>로 재구성
        @SuppressWarnings("unchecked")
        List<BusinessDto> content = (List<BusinessDto>) cachedData.get("content");
        long totalElements = ((Number) cachedData.get("totalElements")).longValue();

        Page<BusinessDto> page = new PageImpl<>(content, pageable, totalElements);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Popular>> searchPopular(
            @RequestParam(defaultValue = "10") int size) {

        // reverseRangeWithScores 높은순으로 정렬
        Set<ZSetOperations.TypedTuple<String>> raw =
                redisTemplate.opsForZSet().reverseRangeWithScores("popular:search", 0, size - 1);

        if (raw == null || raw.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<Popular> result = raw.stream()
                .map(tuple -> new Popular(tuple.getValue(), tuple.getScore().intValue()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/detail")
    public ResponseEntity<BusinessDetailDto> searchBusinessDetail(
            @RequestParam("businessId") Long businessId) {
        BusinessDetailDto result = businessService.findBusinessDetailById(businessId);
        return ResponseEntity.ok(result);
    }

}
