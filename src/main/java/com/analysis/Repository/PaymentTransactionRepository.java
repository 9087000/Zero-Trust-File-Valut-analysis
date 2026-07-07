package com.analysis.Repository;

import com.analysis.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository
        extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByOrderId(String orderId);

    List<PaymentTransaction> findByUsernameOrderByCreatedAtDesc(String username);
}