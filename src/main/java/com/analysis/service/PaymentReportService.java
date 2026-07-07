package com.analysis.service;

import com.analysis.client.FileReportClient;
import com.analysis.dto.PaymentAnalysisResponse;
import com.analysis.dto.ReportWriteRequest;
import com.analysis.entity.PaymentTransaction;
import com.analysis.Repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentReportService {

    private final PaymentTransactionRepository repository;
    private final FileReportClient fileReportClient;

    public PaymentAnalysisResponse analyze(Long id) {
        PaymentTransaction tx = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        String category = detectCategory(tx.getMerchantName());
        String riskScore = "SUCCESS".equalsIgnoreCase(tx.getStatus()) ? "LOW" : "HIGH";

        return new PaymentAnalysisResponse(
                tx.getId(),
                tx.getMerchantName(),
                tx.getAmount(),
                safe(tx.getCurrency(), "INR"),
                tx.getPaymentMode(),
                safe(tx.getProvider(), "RAZORPAY"),
                tx.getStatus(),
                category,
                riskScore,
                "Transaction analyzed successfully",
                tx.getCreatedAt()
        );
    }

    public Mono<ResponseEntity<byte[]>> downloadReport(
            Long id,
            String format,
            String authorization
    ) {
        PaymentTransaction tx = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        PaymentAnalysisResponse analysis = analyze(id);

        String cleanFormat = format == null ? "pdf" : format.toLowerCase();

        String summary = """
                This transaction was analyzed successfully.
                Merchant category is %s.
                Risk score is %s.
                Payment status is %s.
                """.formatted(
                safe(analysis.category()),
                safe(analysis.riskScore()),
                safe(analysis.status())
        );

        String content = """
                ZeroVault Payment Analysis Report

                Transaction Id : %s
                Merchant       : %s
                Amount         : %s %s
                Payment Mode   : %s
                Provider       : %s
                Status         : %s
                Category       : %s
                Risk Score     : %s
                Date           : %s

                Analysis Summary:
                %s
                """.formatted(
                analysis.transactionId(),
                safe(analysis.merchantName()),
                safe(analysis.currency(), "INR"),
                analysis.amount(),
                safe(analysis.paymentMode()),
                safe(analysis.provider(), "RAZORPAY"),
                safe(analysis.status()),
                safe(analysis.category()),
                safe(analysis.riskScore()),
                analysis.transactionDate(),
                summary
        );

        Map<String, Object> row = new LinkedHashMap<>();
        row.put("Transaction Id", analysis.transactionId());
        row.put("Merchant", safe(analysis.merchantName()));
        row.put("Amount", analysis.amount());
        row.put("Currency", safe(analysis.currency(), "INR"));
        row.put("Payment Mode", safe(analysis.paymentMode()));
        row.put("Provider", safe(analysis.provider(), "RAZORPAY"));
        row.put("Status", safe(analysis.status()));
        row.put("Category", safe(analysis.category()));
        row.put("Risk Score", safe(analysis.riskScore()));
        row.put("Analysis Summary", summary.replace("\n", " "));
        row.put("Date", analysis.transactionDate() == null ? "" : analysis.transactionDate().toString());

        ReportWriteRequest request = new ReportWriteRequest(
                "payment-analysis-report-" + id + "." + cleanFormat,
                cleanFormat,
                "ZeroVault Payment Analysis Report",
                content,
                List.of(row)
        );

        return fileReportClient.downloadReport(
                tx.getUsername(),
                request,
                authorization
        );
    }

    private String detectCategory(String merchant) {
        if (merchant == null) return "General";

        String m = merchant.toLowerCase();

        if (m.contains("amazon") || m.contains("flipkart") || m.contains("myntra")) return "Shopping";
        if (m.contains("swiggy") || m.contains("zomato")) return "Food";
        if (m.contains("uber") || m.contains("ola")) return "Travel";
        if (m.contains("netflix") || m.contains("spotify")) return "Subscription";

        return "General";
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}