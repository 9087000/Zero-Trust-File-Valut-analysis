package com.analysis.dto;


public record RazorpayVerifyRequest(
        String razorpayOrderId,
        String razorpayPaymentId,
        String razorpaySignature
) {}