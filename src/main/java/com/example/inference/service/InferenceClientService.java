package com.example.inference.service;

import com.example.inference.model.PromptResult;

public interface InferenceClientService {

    PromptResult infer(String prompt, int index);
}
