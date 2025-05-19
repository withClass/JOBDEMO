package org.example.jobdemo.external;

import lombok.RequiredArgsConstructor;
import org.example.jobdemo.dto.OpenApiBusinessDto;
import org.example.jobdemo.dto.OpenApiResponseWrapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<OpenApiBusinessDto> fetchBusinesses() {
        String baseUrl = "https://api.odcloud.kr/api/15083277/v1/uddi:c6bf89c2-8c0b-4c8e-8698-b2cd9dc31d1f_201908061635";
        String rawServiceKey = "goN%2FzaWnUZhYEiaWb45ux82TGW%2BbdvcdibPSmxKEHJXpFyiEaQR5Lt%2Fcsg0Q7LtqNf15gVpVtKY0rZ%2BQ8Mep8g%3D%3D";

        int totalPages = 500;
        int perPage = 1000;

        List<OpenApiBusinessDto> allData = new ArrayList<>();

        for (int page = 1; page <= totalPages; page++) {
            URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("page", page)
                    .queryParam("perPage", perPage)
                    .queryParam("serviceKey", rawServiceKey)
                    .build(true) // true: 인코딩 방지
                    .toUri();

            try {
                ResponseEntity<OpenApiResponseWrapper> response = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {}
                );

                List<OpenApiBusinessDto> pageData = response.getBody().getData();
                if (pageData != null) {
                    allData.addAll(pageData);
                    System.out.println("✅ Page " + page + " fetched, items: " + pageData.size());
                } else {
                    System.out.println("⚠️ Page " + page + " returned null data");
                }

                // 요청 사이에 휴식 (API Rate Limit 방지)
                Thread.sleep(1000); // 1초 휴식

            } catch (Exception e) {
                System.err.println("❌ Failed to fetch page " + page + ": " + e.getMessage());
            }
        }

        return allData;
    }
}
