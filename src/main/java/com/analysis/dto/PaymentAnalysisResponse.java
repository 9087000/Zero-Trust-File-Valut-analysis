package com.analysis.dto;

import java.time.Instant;

public record PaymentAnalysisResponse(
        Long transactionId,
        String merchantName,
        Double amount,
        String currency,
        String paymentMode,
        String provider,
        String status,
        String category,
        String riskScore,
        String message,
        Instant transactionDate
) {
}