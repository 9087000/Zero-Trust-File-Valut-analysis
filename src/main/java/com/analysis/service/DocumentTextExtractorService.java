package com.analysis.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class DocumentTextExtractorService {

    public String extract(String fileType, InputStream inputStream) {
        try {
            return switch (fileType.toLowerCase()) {
                case "pdf" -> extractPdf(inputStream);
                case "docx" -> extractDocx(inputStream);
                case "xlsx" -> extractXlsx(inputStream);
                default -> new String(inputStream.readAllBytes());
            };
        } catch (Exception e) {
            throw new RuntimeException("Text extraction failed: " + e.getMessage(), e);
        }
    }

    private String extractPdf(InputStream inputStream) throws Exception {
        byte[] bytes = inputStream.readAllBytes();

        try (var document = Loader.loadPDF(bytes)) {
            return new PDFTextStripper().getText(document);
        }
    }

    private String extractDocx(InputStream inputStream) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(inputStream)) {
            StringBuilder sb = new StringBuilder();

            doc.getParagraphs().forEach(p -> sb.append(p.getText()).append("\n"));

            return sb.toString();
        }
    }

    private String extractXlsx(InputStream inputStream) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            StringBuilder sb = new StringBuilder();

            workbook.forEach(sheet ->
                    sheet.forEach(row -> {
                        row.forEach(cell -> sb.append(cell.toString()).append(" | "));
                        sb.append("\n");
                    })
            );

            return sb.toString();
        }
    }
}