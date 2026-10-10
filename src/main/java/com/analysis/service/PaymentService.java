package com.analysis.service;


import com.analysis.Repository.PaymentTransactionRepository;
import com.analysis.dto.CreatePaymentRequest;
import com.analysis.dto.PaymentResponse;
import com.analysis.entity.PaymentTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentTransactionRepository repository;

    public PaymentResponse pay(String username, CreatePaymentRequest req) {

        PaymentTransaction tx = PaymentTransaction.builder()
                .username(username)
                .merchantName(req.merchantName())
                .amount(req.amount())
                .currency(req.currency() == null ? "INR" : req.currency())
                .paymentMode(req.paymentMode())
                .provider("MOCK")
                .orderId("ORD_" + UUID.randomUUID())
                .paymentId("PAY_" + UUID.randomUUID())
                .status("SUCCESS")
                .createdAt(Instant.now())
                .build();

        PaymentTransaction saved = repository.save(tx);

        return new PaymentResponse(
                saved.getId(),
                saved.getOrderId(),
                saved.getPaymentId(),
                saved.getStatus(),
                "Mock payment completed successfully"
        );
    }
}