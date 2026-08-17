package com.vetcare.vetcaremedicalrecordservice.controller;

import com.vetcare.vetcaremedicalrecordservice.entity.MedicalRecord;
import com.vetcare.vetcaremedicalrecordservice.repository.MedicalRecordRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/medical-records")
public class MedicalRecordController {

    private final MedicalRecordRepository repository;

    public MedicalRecordController(
            MedicalRecordRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<MedicalRecord> create(
            @RequestBody MedicalRecord record) {

        record.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.ok(repository.save(record));
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecord>> getAll() {

        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> getById(
            @PathVariable String id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<MedicalRecord>> getByPet(
            @PathVariable Long petId) {

        return ResponseEntity.ok(
                repository.findByPetId(petId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}