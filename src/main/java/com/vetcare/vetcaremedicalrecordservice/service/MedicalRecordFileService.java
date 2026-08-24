package com.vetcare.vetcaremedicalrecordservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class MedicalRecordFileService {

    private final Path uploadDirectory;

    public MedicalRecordFileService(
            @Value("${medical-record.upload-dir:uploads/medical-records}")
            String uploadDir) {

        this.uploadDirectory =
                Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not create upload directory",
                    e
            );
        }
    }

    public String saveFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Medical record image is required."
            );
        }

        String originalName = file.getOriginalFilename();

        String extension = "";

        if (originalName != null &&
                originalName.contains(".")) {

            extension = originalName.substring(
                    originalName.lastIndexOf(".")
            );
        }

        String fileName =
                UUID.randomUUID() + extension;

        Path destination =
                uploadDirectory.resolve(fileName);

        try {

            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return fileName;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to save medical record image",
                    e
            );
        }
    }

    public ResourceFile loadFile(String fileName) {

        try {

            Path file =
                    uploadDirectory
                            .resolve(fileName)
                            .normalize();

            if (!file.startsWith(uploadDirectory)) {
                throw new IllegalArgumentException(
                        "Invalid file path."
                );
            }

            if (!Files.exists(file)) {
                return null;
            }

            return new ResourceFile(
                    Files.readAllBytes(file),
                    Files.probeContentType(file)
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to read medical record image",
                    e
            );
        }
    }

    public void deleteFile(String fileName) {

        if (fileName == null ||
                fileName.isBlank()) {
            return;
        }

        try {

            Path file =
                    uploadDirectory
                            .resolve(fileName)
                            .normalize();

            if (file.startsWith(uploadDirectory)) {
                Files.deleteIfExists(file);
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to delete medical record image",
                    e
            );
        }
    }

    public record ResourceFile(
            byte[] data,
            String contentType
    ) {
    }
}