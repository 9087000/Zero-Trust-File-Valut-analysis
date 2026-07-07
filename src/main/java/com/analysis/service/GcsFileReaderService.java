package com.analysis.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

@Service
public class GcsFileReaderService {

    @Value("${zerovault.storage.bucket}")
    private String bucket;

    @Value("${gcp.credentials.path}")
    private String credentialsPath;

    private Storage storage;

    @PostConstruct
    public void init() throws Exception {
        System.out.println("Using GCP key = " + credentialsPath);

        GoogleCredentials credentials =
                GoogleCredentials.fromStream(new FileInputStream(credentialsPath));

        this.storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }

    public InputStream read(String objectKey) {
        String cleanKey = objectKey == null ? "" : objectKey.trim();

        Blob blob = storage.get(BlobId.of(bucket, cleanKey));

        if (blob == null || !blob.exists()) {
            throw new IllegalArgumentException("File not found in GCS: " + cleanKey);
        }

        return new ByteArrayInputStream(blob.getContent());
    }
}