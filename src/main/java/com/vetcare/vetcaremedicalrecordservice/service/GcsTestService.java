package com.vetcare.vetcaremedicalrecordservice.service;

import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GcsTestService {

    private final Storage storage;
    private final String bucketName;

    public GcsTestService(
            Storage storage,
            @Value("${gcp.storage.bucket-name}") String bucketName
    ) {
        this.storage = storage;
        this.bucketName = bucketName;
    }

    public boolean bucketExists() {
        return storage.get(bucketName) != null;
    }
}