package com.example.inference.repository;

import com.example.inference.model.BatchJob;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BatchJobRepository {

    private final Map<String, BatchJob> store = new ConcurrentHashMap<>();

    public BatchJob save(BatchJob batchJob) {
        store.put(batchJob.getBatchId(), batchJob);
        return batchJob;
    }

    public Optional<BatchJob> findById(String batchId) {
        return Optional.ofNullable(store.get(batchId));
    }

    public void deleteById(String batchId) {
        store.remove(batchId);
    }

    public int count() {
        return store.size();
    }
}
