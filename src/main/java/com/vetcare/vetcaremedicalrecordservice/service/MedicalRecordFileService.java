package com.vetcare.vetcaremedicalrecordservice.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class MedicalRecordFileService {

    private final Storage storage;

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    public MedicalRecordFileService(Storage storage) {
        this.storage = storage;
    }

    // Record Class for loading file byte data
    public record ResourceFile(byte[] data, String contentType) {}

    // 1. Upload File to GCP Cloud Storage Bucket
    public String saveFile(MultipartFile file) {
        try {
            System.out.println("=== CURRENT GCP BUCKET NAME: " + bucketName + " ===");

            String extension = "";
            String originalName = file.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String uniqueFileName = UUID.randomUUID().toString() + extension;
            BlobId blobId = BlobId.of(bucketName, uniqueFileName);

            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());

            System.out.println("=== UPLOAD SUCCESSFUL TO BUCKET: " + bucketName + " FILENAME: " + uniqueFileName + " ===");
            return uniqueFileName;
        } catch (Exception e) {
            System.out.println("=== UPLOAD FAILED ERROR: " + e.getMessage() + " ===");
            e.printStackTrace();
            throw new RuntimeException("GCP Cloud Storage Upload Failed: " + e.getMessage(), e);
        }
    }

    // 2. Download/Stream File from GCP Bucket
    public ResourceFile loadFile(String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        Blob blob = storage.get(blobId);

        if (blob == null || !blob.exists()) {
            return null;
        }

        byte[] content = blob.getContent();
        String contentType = blob.getContentType();

        return new ResourceFile(content, contentType);
    }

    // 3. Delete File from GCP Bucket
    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        BlobId blobId = BlobId.of(bucketName, fileName);
        storage.delete(blobId);
    }
}