package org.example.ai.models;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;

import java.util.Optional;

public class OpenAiTextModelAdapter implements TextModelAdapter {

    private final OpenAIClient client;

    public OpenAiTextModelAdapter() {
        this.client = OpenAIOkHttpClient.fromEnv();
    }

    private ResponseCreateParams getCreateParams(final String prompt) {
        return ResponseCreateParams.builder()
                .input(prompt)
                .model(ChatModel.GPT_4O)
                .build();
    }

    @Override
    public Optional<String> query(String prompt) {
        return client.responses()
                .create(getCreateParams(prompt))
                .output()
                .stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .findFirst()
                .map(content -> content.asOutputText().text());
    }
}
