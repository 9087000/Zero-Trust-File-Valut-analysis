package com.analysis.dto;


public record PaymentVerifyResponse(
        Long transactionId,
        String orderId,
        String paymentId,
        String status,
        String message
) {}