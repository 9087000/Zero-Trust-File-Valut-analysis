package com.analysis.service;

import com.analysis.Repository.PaymentTransactionRepository;
import com.analysis.dto.CreatePaymentRequest;
import com.analysis.dto.PaymentVerifyResponse;
import com.analysis.dto.RazorpayOrderResponse;
import com.analysis.dto.RazorpayVerifyRequest;
import com.analysis.entity.PaymentTransaction;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RazorpayPaymentService {

    private final PaymentTransactionRepository repository;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    public RazorpayOrderResponse createOrder(
            String username,
            CreatePaymentRequest request
    ) throws Exception {

        String currency = request.currency() == null || request.currency().isBlank()
                ? "INR"
                : request.currency();

        int amountInPaise = (int) Math.round(request.amount() * 100);

        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        String receipt = "RCPT" + System.currentTimeMillis();

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", receipt);

        Order order = client.orders.create(orderRequest);

        String orderId = order.get("id");
        String status = order.get("status");

        PaymentTransaction tx = PaymentTransaction.builder()
                .username(username)
                .merchantName(request.merchantName())
                .amount(request.amount())
                .currency(currency)
                .paymentMode(request.paymentMode())
                .provider("RAZORPAY")
                .orderId(orderId)
                .status("CREATED")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        PaymentTransaction saved = repository.save(tx);

        return new RazorpayOrderResponse(
                saved.getId(),
                orderId,
                keyId,
                request.amount(),
                currency,
                status
        );
    }

    public PaymentVerifyResponse verifyPayment(RazorpayVerifyRequest request) throws Exception {

        if (request.razorpayOrderId() == null || request.razorpayOrderId().isBlank()
                || request.razorpayPaymentId() == null || request.razorpayPaymentId().isBlank()
                || request.razorpaySignature() == null || request.razorpaySignature().isBlank()) {

            return new PaymentVerifyResponse(
                    null,
                    request.razorpayOrderId(),
                    request.razorpayPaymentId(),
                    "FAILED",
                    "Missing Razorpay order id, payment id, or signature. Verify API should be called after Razorpay Checkout success."
            );
        }

        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_order_id", request.razorpayOrderId());
        attributes.put("razorpay_payment_id", request.razorpayPaymentId());
        attributes.put("razorpay_signature", request.razorpaySignature());

        boolean valid = Utils.verifyPaymentSignature(attributes, keySecret);

        PaymentTransaction tx = repository.findByOrderId(request.razorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!valid) {
            tx.setStatus("FAILED");
            tx.setPaymentId(request.razorpayPaymentId());
            tx.setUpdatedAt(Instant.now());
            repository.save(tx);

            return new PaymentVerifyResponse(
                    tx.getId(),
                    tx.getOrderId(),
                    request.razorpayPaymentId(),
                    "FAILED",
                    "Invalid Razorpay signature"
            );
        }

        tx.setPaymentId(request.razorpayPaymentId());
        tx.setStatus("SUCCESS");
        tx.setUpdatedAt(Instant.now());
        repository.save(tx);

        return new PaymentVerifyResponse(
                tx.getId(),
                tx.getOrderId(),
                tx.getPaymentId(),
                tx.getStatus(),
                "Payment verified successfully"
        );
    }

    public java.util.List<PaymentTransaction> history(String username) {
        return repository.findByUsernameOrderByCreatedAtDesc(username);
    }
}