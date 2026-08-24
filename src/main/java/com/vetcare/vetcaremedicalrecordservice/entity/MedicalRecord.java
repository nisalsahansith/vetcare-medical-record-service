package com.vetcare.vetcaremedicalrecordservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "medical_records")
public class MedicalRecord {

    @Id
    private String id;

    private Long petId;

    private Long vetId;

    private String diagnosis;

    private String treatment;

    private String prescription;

    private String notes;

    private String imagePath;

    private String imageName;

    private LocalDateTime createdAt;

    public MedicalRecord() {
    }

    public MedicalRecord(String id, Long petId, Long vetId, String diagnosis, String treatment, String prescription, String notes, String imagePath, String imageName, LocalDateTime createdAt) {
        this.id = id;
        this.petId = petId;
        this.vetId = vetId;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.prescription = prescription;
        this.notes = notes;
        this.imagePath = imagePath;
        this.imageName = imageName;
        this.createdAt = createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public void setVetId(Long vetId) {
        this.vetId = vetId;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public Long getPetId() {
        return petId;
    }

    public Long getVetId() {
        return vetId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getPrescription() {
        return prescription;
    }

    public String getNotes() {
        return notes;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}