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
        String baseUrl = "https://api.odcloud.kr/api/15083277/v1/uddi:c70b85ac-0146-41a9-8f4a-d2acafaa3c92";
        String rawServiceKey = System.getenv("KEY");

        int perPage = 3000;
        int totalPages = 1; // 초기값, 첫 페이지 요청 후 결정

        List<OpenApiBusinessDto> allData = new ArrayList<>();

        for (int page = 1; page <= totalPages; page++) {
            URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                    .queryParam("page", page)
                    .queryParam("perPage", perPage)
                    .queryParam("serviceKey", rawServiceKey)
                    .build(true)
                    .toUri();

            try {
                ResponseEntity<OpenApiResponseWrapper> response = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {}
                );

                OpenApiResponseWrapper body = response.getBody();

                if (body != null && body.getData() != null) {
                    if (page == 1 && body.getTotalCount() > 0) {
                        totalPages = (int) Math.ceil((double) body.getTotalCount() / perPage);
                        System.out.println("📊 Total count: " + body.getTotalCount() + " → Pages: " + totalPages);
                    }

                    allData.addAll(body.getData());
                    System.out.println("✅ Page " + page + " fetched, items: " + body.getData().size());
                } else {
                    System.out.println("⚠️ Page " + page + " returned null");
                }

                Thread.sleep(200); // API rate limit 대응

            } catch (Exception e) {
                System.err.println("❌ Failed to fetch page " + page + ": " + e.getMessage());
            }
        }

        return allData;
    }
}

