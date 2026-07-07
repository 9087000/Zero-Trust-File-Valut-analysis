package com.analysis.dto;

import java.util.List;
import java.util.Map;

public record ReportWriteRequest(
        String fileName,
        String fileType,
        String title,
        String content,
        List<Map<String, Object>> rows
) {}