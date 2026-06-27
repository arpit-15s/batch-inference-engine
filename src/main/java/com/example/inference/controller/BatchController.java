package com.example.inference.controller;

import com.example.inference.dto.BatchAckResponse;
import com.example.inference.dto.BatchRequest;
import com.example.inference.dto.BatchResultResponse;
import com.example.inference.service.BatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/batches")
@Tag(name = "Batch", description = "Batch inference operations")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @PostMapping
    @Operation(summary = "Submit a batch of prompts for inference")
    public ResponseEntity<BatchAckResponse> submitBatch(@Valid @RequestBody BatchRequest request) {
        BatchAckResponse response = batchService.acceptBatch(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{batchId}")
    @Operation(summary = "Get batch status and aggregated results")
    public ResponseEntity<BatchResultResponse> getBatch(@PathVariable String batchId) {
        BatchResultResponse response = batchService.getBatch(batchId);
        return ResponseEntity.ok(response);
    }
}
