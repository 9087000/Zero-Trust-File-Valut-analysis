package com.analysis.dto;


public record PaymentResponse(
        Long id,
        String orderId,
        String paymentId,
        String status,
        String message
) {}