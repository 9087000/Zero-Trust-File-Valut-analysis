package com.analysis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "payment_transaction")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String merchantName;

    private Double amount;
    private String currency;
    private String paymentMode;

    private String provider;

    @Column(unique = true)
    private String orderId;

    private String paymentId;
    private String status;

    private Instant createdAt;
    private Instant updatedAt;
}