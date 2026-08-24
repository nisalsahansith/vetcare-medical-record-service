package com.vetcare.vetcaremedicalrecordservice.controller;

import com.vetcare.vetcaremedicalrecordservice.entity.MedicalRecord;
import com.vetcare.vetcaremedicalrecordservice.repository.MedicalRecordRepository;
import com.vetcare.vetcaremedicalrecordservice.service.MedicalRecordFileService;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/medical-records")
public class MedicalRecordController {

    private final MedicalRecordRepository repository;
    private final MedicalRecordFileService fileService;

    public MedicalRecordController(
            MedicalRecordRepository repository,
            MedicalRecordFileService fileService) {

        this.repository = repository;
        this.fileService = fileService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MedicalRecord> create(
            @RequestParam Long petId,
            @RequestParam Long vetId,
            @RequestParam(required = false) String diagnosis,
            @RequestParam(required = false) String treatment,
            @RequestParam(required = false) String prescription,
            @RequestParam(required = false) String notes,
            @RequestParam MultipartFile image) {

        String fileName =
                fileService.saveFile(image);

        MedicalRecord record =
                new MedicalRecord();

        record.setPetId(petId);
        record.setVetId(vetId);
        record.setDiagnosis(diagnosis);
        record.setTreatment(treatment);
        record.setPrescription(prescription);
        record.setNotes(notes);

        record.setImageName(
                image.getOriginalFilename()
        );

        record.setImagePath(
                "/api/v1/medical-records/files/"
                        + fileName
        );

        record.setCreatedAt(
                LocalDateTime.now()
        );

        return ResponseEntity.ok(
                repository.save(record)
        );
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecord>> getAll() {

        return ResponseEntity.ok(
                repository.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> getById(
            @PathVariable String id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<MedicalRecord>> getByPet(
            @PathVariable Long petId) {

        return ResponseEntity.ok(
                repository.findByPetId(petId)
        );
    }

    @GetMapping("/files/{fileName}")
    public ResponseEntity<ByteArrayResource> getFile(
            @PathVariable String fileName) {

        MedicalRecordFileService.ResourceFile file =
                fileService.loadFile(fileName);

        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType =
                MediaType.APPLICATION_OCTET_STREAM;

        if (file.contentType() != null) {
            try {
                mediaType =
                        MediaType.parseMediaType(
                                file.contentType()
                        );
            } catch (Exception ignored) {
            }
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline"
                )
                .body(
                        new ByteArrayResource(
                                file.data()
                        )
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(
            @PathVariable String id) {

        return repository.findById(id)
                .map(record -> {

                    fileService.deleteFile(
                            extractFileName(
                                    record.getImagePath()
                            )
                    );

                    repository.deleteById(id);

                    return ResponseEntity
                            .<Void>noContent()
                            .build();
                })
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    private String extractFileName(
            String imagePath) {

        if (imagePath == null ||
                imagePath.isBlank()) {

            return null;
        }

        int index =
                imagePath.lastIndexOf("/");

        if (index == -1) {
            return imagePath;
        }

        return imagePath.substring(index + 1);
    }
}