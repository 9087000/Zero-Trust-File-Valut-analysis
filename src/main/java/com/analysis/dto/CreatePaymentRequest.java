package com.analysis.dto;

public record CreatePaymentRequest(
        String merchantName,
        Double amount,
        String currency,
        String paymentMode
) {}