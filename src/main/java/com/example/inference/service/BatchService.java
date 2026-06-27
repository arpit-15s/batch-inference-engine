package com.example.inference.service;

import com.example.inference.dto.BatchAckResponse;
import com.example.inference.dto.BatchRequest;
import com.example.inference.dto.BatchResultResponse;

public interface BatchService {

    BatchAckResponse acceptBatch(BatchRequest request);

    BatchResultResponse getBatch(String batchId);
}
