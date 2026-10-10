package com.analysis.dto;

public record AnalysisResponse(
        Long fileId,
        String status,
        String summary
) {
}
