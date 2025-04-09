package org.example.ai;

import com.google.genai.Client;

public class GoogleTeamDescriptionServiceImpl implements TeamDescriptionService {
    private static final String TEAM_DESCRIPTION_PROMPT = "Give me a description of the following football team: %s." +
            " The description should be short and concise, focusing on the performance of the team in the last years and the fans it has." +
            " If the required name is not from a football team, output the value 'description not found' and nothing else." +
            " Only show information relevant to a football fan who is interested in how the team is performing.";

    private static final String DEFAULT = "No description found";

    Client client;

    public GoogleTeamDescriptionServiceImpl() {
        client = new Client();
    }

    @Override
    public String getTeamDescription(String teamName) {
        try {
            return client.models.generateContent("gemini-2.0-flash-001", String.format(TEAM_DESCRIPTION_PROMPT,teamName),null).text();
        }catch (Exception e){
            return DEFAULT;
        }

    }
}
