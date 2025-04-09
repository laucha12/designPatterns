package org.example.ai.models;

import java.util.Optional;

//Interface used in the adapters and in the TeamDescriptionServiceImpl
public interface TextModelAdapter {
    Optional<String> query(final String prompt);
}
