package com.analysis.dto;

public record AnalysisRequest(
        Long fileId,
        String userName,
        String fileName,
        String objectKey,
        String fileType
) {}
