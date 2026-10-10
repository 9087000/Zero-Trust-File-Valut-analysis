package com.analysis.dto;

public record RazorpayOrderResponse(
        Long transactionId,
        String orderId,
        String keyId,
        Double amount,
        String currency,
        String status
) {}