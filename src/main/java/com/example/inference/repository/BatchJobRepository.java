package com.example.inference.repository;

import com.example.inference.model.BatchJob;

import java.util.Optional;

public interface BatchJobRepository {

    BatchJob save(BatchJob batchJob);

    Optional<BatchJob> findById(String batchId);

    void deleteById(String batchId);

    int count();
}
