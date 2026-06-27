package com.example.inference.service;

import com.example.inference.model.PromptResult;
import org.springframework.stereotype.Service;

@Service
public class InferenceClientService {

    public PromptResult infer(String prompt, int index) {
        // TODO: implement rate-limited inference call with retry on 429/5xx
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
