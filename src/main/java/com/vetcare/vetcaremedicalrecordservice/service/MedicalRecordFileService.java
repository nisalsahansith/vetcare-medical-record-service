package com.vetcare.vetcaremedicalrecordservice.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class MedicalRecordFileService {

    private final Storage storage;
    private final String bucketName = "vetcare-files-project-e9fb8820-8459-4622-9e9";

    public MedicalRecordFileService(Storage storage) {
        this.storage = storage;
    }

    public record ResourceFile(byte[] data, String contentType) {}

    public String saveFile(MultipartFile file) {
        try {
            System.out.println("==========================================");
            System.out.println("=== ATTEMPTING GCS UPLOAD TO BUCKET: " + bucketName + " ===");
            System.out.println("==========================================");

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

            System.out.println("==========================================");
            System.out.println("=== GCS UPLOAD SUCCESSFUL! FILE: " + uniqueFileName + " ===");
            System.out.println("==========================================");

            return uniqueFileName;

        } catch (Exception e) {
            System.err.println("==========================================");
            System.err.println("=== GCS UPLOAD FAILED ERROR: " + e.getMessage() + " ===");
            e.printStackTrace();
            System.err.println("==========================================");
            throw new RuntimeException("GCP Cloud Storage Upload Failed: " + e.getMessage(), e);
        }
    }

    public ResourceFile loadFile(String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        var blob = storage.get(blobId);

        if (blob == null || !blob.exists()) {
            return null;
        }

        return new ResourceFile(blob.getContent(), blob.getContentType());
    }

    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        BlobId blobId = BlobId.of(bucketName, fileName);
        storage.delete(blobId);
    }
}
