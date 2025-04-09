package org.example.ai.models;

import com.google.genai.Client;

import java.util.Optional;

public class GoogleTextModelAdapter implements TextModelAdapter {

    private static final String MODEL_NAME = "gemini-2.0-flash-001";

    private final Client client;

    public GoogleTextModelAdapter() {
        client = new Client();
    }

    @Override
    public Optional<String> query(String prompt) {
        try {
            return Optional.ofNullable(client.models.generateContent(MODEL_NAME, prompt,null).text());
        }catch (Exception e){
            return Optional.empty();
        }
    }
}
