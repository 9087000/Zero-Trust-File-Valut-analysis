package com.analysis.Repository;

import com.analysis.entity.DocumentAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentAnalysisRepository extends JpaRepository<DocumentAnalysis, Long> {
}
