package org.example.ai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;

public class OpenAiTeamDescriptionServiceImpl implements TeamDescriptionService {

    private static final String TEAM_DESCRIPTION_PROMPT = "Give me a description of the following football team: %s." +
            " The description should be short and concise, focusing on the performance of the team in the last years and the fans it has." +
            " If the required name is not from a football team, output the value 'description not found' and nothing else." +
            " Only show information relevant to a football fan who is interested in how the team is performing.";

    private static final String DEFAULT = "No description found";

    OpenAIClient client;

    public OpenAiTeamDescriptionServiceImpl() {
        this.client = OpenAIOkHttpClient.fromEnv();
    }

    private ResponseCreateParams getCreateParams(final String teamName) {
        return ResponseCreateParams.builder()
                .input(String.format(TEAM_DESCRIPTION_PROMPT, teamName))
                .model(ChatModel.GPT_4O)
                .build();
    }

    @Override
    public String getTeamDescription(final String teamName) {
            return client.responses()
                    .create(getCreateParams(teamName))
                    .output()
                    .stream()
                    .flatMap(item -> item.message().stream())
                    .flatMap(message -> message.content().stream())
                    .findFirst()
                    .map(content -> content.asOutputText().text())
                    .orElse(DEFAULT);
    }
}
