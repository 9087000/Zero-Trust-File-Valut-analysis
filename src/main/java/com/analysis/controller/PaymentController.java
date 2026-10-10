package com.analysis.controller;

import com.analysis.dto.*;
import com.analysis.entity.PaymentTransaction;
import com.analysis.security.JwtService;
import com.analysis.service.PaymentReportService;
import com.analysis.service.RazorpayPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayPaymentService razorpayPaymentService;
    private final JwtService jwtService;
    private final PaymentReportService paymentReportService;

    @PostMapping("/razorpay/order")
    public RazorpayOrderResponse createRazorpayOrder(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody CreatePaymentRequest request
    ) throws Exception {
        String username = extractUsername(authorization);
        return razorpayPaymentService.createOrder(username, request);
    }

    @PostMapping("/razorpay/verify")
    public PaymentVerifyResponse verifyRazorpayPayment(
            @RequestBody RazorpayVerifyRequest request
    ) throws Exception {
        return razorpayPaymentService.verifyPayment(request);
    }

    @GetMapping("/history")
    public List<PaymentTransaction> history(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        String username = extractUsername(authorization);
        return razorpayPaymentService.history(username);
    }

    @GetMapping("/{id}/analysis")
    public PaymentAnalysisResponse analyzePayment(@PathVariable Long id) {
        return paymentReportService.analyze(id);
    }

    @GetMapping("/{id}/report/{format}")
    public Mono<ResponseEntity<byte[]>> downloadPaymentReport(
            @PathVariable Long id,
            @PathVariable String format,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        return paymentReportService.downloadReport(id, format, authorization);
    }

    private String extractUsername(String authorization) {
        String token = authorization.replace("Bearer ", "");
        return jwtService.username(token);
    }
}