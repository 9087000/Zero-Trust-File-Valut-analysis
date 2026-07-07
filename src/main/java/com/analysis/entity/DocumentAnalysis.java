package com.analysis.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fileId;
    private String userName;
    private String fileName;
    private String fileType;

    @Column(columnDefinition = "TEXT")
    private String extractedText;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private String status;
    private Instant createdAt;
}
