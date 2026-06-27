package com.example.inference.service;

import com.example.inference.dto.BatchResultResponse;
import com.example.inference.model.BatchJob;
import com.example.inference.model.PromptResult;

import java.util.List;

public interface AggregationService {

    BatchResultResponse aggregate(BatchJob batchJob, List<PromptResult> promptResults);

    BatchResultResponse toResponse(BatchJob batchJob);
}
