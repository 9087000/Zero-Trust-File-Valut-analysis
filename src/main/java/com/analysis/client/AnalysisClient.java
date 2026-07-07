package com.analysis.client;


import com.analysis.dto.AnalysisRequest;
import com.analysis.dto.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnalysisClient {

    private final WebClient webClient;

    public Mono<AnalysisResponse> analyze(
            AnalysisRequest request,
            String authorization
    ) {

        return webClient.post()
                .uri("http://localhost:8084/api/analysis/documents")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        authorization
                )
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AnalysisResponse.class);
    }
}
