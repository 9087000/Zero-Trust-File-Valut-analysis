package com.analysis.client;

import com.analysis.dto.ReportWriteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FileReportClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${zerovault.file-service-url:http://localhost:8083}")
    private String fileServiceUrl;

    public Mono<ResponseEntity<byte[]>> downloadReport(
            String username,
            ReportWriteRequest request,
            String authorization
    ) {
        return webClientBuilder.build()
                .post()
                .uri(fileServiceUrl + "/api/files/users/{username}/documents/write/download", username)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .toEntity(byte[].class);
    }
}