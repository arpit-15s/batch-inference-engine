package com.example.inference.service;

import com.example.inference.dto.BatchResultResponse;
import com.example.inference.model.BatchJob;
import com.example.inference.model.PromptResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AggregationService {

    public BatchResultResponse aggregate(BatchJob batchJob, List<PromptResult> promptResults) {
        // TODO: compile successful inferences into response JSON structure
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
