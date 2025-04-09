package org.example.ai;

import org.example.ai.models.TextModelAdapter;

public class TeamDescriptionServiceImpl implements TeamDescriptionService {

    private static final String TEAM_DESCRIPTION_PROMPT = "Give me a description of the following football team: %s." +
            " The description should be short and concise, focusing on the performance of the team in the last years and the fans it has." +
            " If the required name is not from a football team, output the value 'description not found' and nothing else." +
            " Only show information relevant to a football fan who is interested in how the team is performing.";

    private static final String DEFAULT = "No description found";

    private final TextModelAdapter model;

    public TeamDescriptionServiceImpl(TextModelAdapter model) {
        this.model = model;
    }

    @Override
    public String getTeamDescription(String teamName) {
        return model.query(String.format(TEAM_DESCRIPTION_PROMPT,teamName)).orElse(DEFAULT);
    }
}
