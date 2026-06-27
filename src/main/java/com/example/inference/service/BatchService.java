package com.example.inference.service;

import com.example.inference.dto.BatchAckResponse;
import com.example.inference.dto.BatchRequest;
import com.example.inference.dto.BatchResultResponse;
import com.example.inference.repository.BatchJobRepository;
import org.springframework.stereotype.Service;

@Service
public class BatchService {

    private final BatchJobRepository batchJobRepository;
    private final InferenceClientService inferenceClientService;
    private final AggregationService aggregationService;

    public BatchService(BatchJobRepository batchJobRepository,
                        InferenceClientService inferenceClientService,
                        AggregationService aggregationService) {
        this.batchJobRepository = batchJobRepository;
        this.inferenceClientService = inferenceClientService;
        this.aggregationService = aggregationService;
    }

    public BatchAckResponse acceptBatch(BatchRequest request) {
        // TODO: implement batch acceptance and async processing
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public BatchResultResponse getBatch(String batchId) {
        // TODO: implement batch lookup and response mapping
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
