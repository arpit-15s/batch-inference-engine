package com.example.inference.service;

import com.example.inference.dto.BatchResultResponse;
import com.example.inference.model.BatchJob;
import com.example.inference.model.JobStatus;
import com.example.inference.model.PromptResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AggregationServiceImplTest {

    private final AggregationServiceImpl aggregationService = new AggregationServiceImpl();

    @Test
    void aggregateIncludesOnlySuccessfulResults() {
        BatchJob job = new BatchJob();
        job.setBatchId("batch-1");
        job.setTotalPrompts(2);

        PromptResult success = new PromptResult();
        success.setIndex(0);
        success.setPrompt("a");
        success.setInference("result-a");
        success.setLatencyMs(100);
        success.setSuccess(true);

        PromptResult failure = new PromptResult();
        failure.setIndex(1);
        failure.setPrompt("b");
        failure.setSuccess(false);

        BatchResultResponse response = aggregationService.aggregate(job, List.of(success, failure));

        assertThat(job.getStatus()).isEqualTo(JobStatus.PARTIAL);
        assertThat(response.getSummary().getSucceeded()).isEqualTo(1);
        assertThat(response.getSummary().getFailed()).isEqualTo(1);
        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResults().get(0).getInference()).isEqualTo("result-a");
    }
}
