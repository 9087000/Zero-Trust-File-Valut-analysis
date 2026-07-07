package com.analysis.service;


import com.analysis.Repository.DocumentAnalysisRepository;
import com.analysis.dto.AnalysisRequest;
import com.analysis.dto.AnalysisResponse;
import com.analysis.entity.DocumentAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DocumentAnalysisService {

    private final GcsFileReaderService gcsFileReaderService;
    private final DocumentTextExtractorService extractorService;
    private final DocumentAnalysisRepository repository;

    public Mono<AnalysisResponse> analyze(AnalysisRequest request) {
        return Mono.fromCallable(() -> {
            var inputStream = gcsFileReaderService.read(request.objectKey());

            String text = extractorService.extract(request.fileType(), inputStream);
            String cleanedText = sanitizeForPostgres(text);
            String summary = createSimpleSummary(cleanedText);

            DocumentAnalysis analysis = DocumentAnalysis.builder()
                    .fileId(request.fileId())
                    .userName(request.userName())
                    .fileName(request.fileName())
                    .fileType(request.fileType())
                    .extractedText(text)
                    .summary(summary)
                    .status("COMPLETED")
                    .createdAt(Instant.now())
                    .build();

            repository.save(analysis);

            return new AnalysisResponse(
                    request.fileId(),
                    "COMPLETED",
                    summary
            );
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private String sanitizeForPostgres(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (char c : text.toCharArray()) {
            // PostgreSQL TEXT cannot store null byte
            if (c == '\u0000') {
                continue;
            }

            // Remove control characters except useful whitespace
            if (Character.isISOControl(c)
                    && c != '\n'
                    && c != '\r'
                    && c != '\t') {
                continue;
            }

            sb.append(c);
        }

        return sb.toString()
                .replace("\u0000", "")
                .trim();
    }

    private String createSimpleSummary(String text) {
        if (text == null || text.isBlank()) {
            return "No readable text found.";
        }

        String clean = text.replaceAll("\\s+", " ").trim();

        return clean.length() > 800
                ? clean.substring(0, 800) + "..."
                : clean;
    }
}