package com.vetcare.vetcaremedicalrecordservice.repository;

import com.vetcare.vetcaremedicalrecordservice.entity.MedicalRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MedicalRecordRepository
        extends MongoRepository<MedicalRecord, String> {

    List<MedicalRecord> findByPetId(Long petId);

    List<MedicalRecord> findByVetId(Long vetId);
}