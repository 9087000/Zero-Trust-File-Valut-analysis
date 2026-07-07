package com.analysis.controller;


import com.analysis.dto.AnalysisRequest;
import com.analysis.dto.AnalysisResponse;
import com.analysis.service.DocumentAnalysisService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@SecurityRequirement(name="bearerAuth")
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final DocumentAnalysisService documentAnalysisService;

    @PostMapping("/documents")
    public Mono<ResponseEntity<AnalysisResponse>> analyzeDocument(
            @RequestBody AnalysisRequest request
    ) {
        return documentAnalysisService
                .analyze(request)
                .map(ResponseEntity::ok);
    }
}
