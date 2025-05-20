package org.example.jobdemo.controller;

import lombok.RequiredArgsConstructor;
import org.example.jobdemo.entity.Business;
import org.example.jobdemo.service.BusinessService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

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
    public ResponseEntity<Page<Business>> searchBusinessesCached(
            @RequestParam("keyword") String keyword,
            Pageable pageable) {

        Map<String, Object> cachedData = businessService.searchBusinessesByNameCached(keyword, pageable);

        // 캐시된 데이터에서 Page<Business>로 재구성
        @SuppressWarnings("unchecked")
        List<Business> content = (List<Business>) cachedData.get("content");
        long totalElements = ((Number) cachedData.get("totalElements")).longValue();

        Page<Business> page = new PageImpl<>(content, pageable, totalElements);

        return ResponseEntity.ok(page);
    }


}
