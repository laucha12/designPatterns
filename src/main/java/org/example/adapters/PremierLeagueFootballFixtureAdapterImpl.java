package org.example.adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.interfaces.FootballFixtureAdapter;
import org.example.interfaces.TeamRepository;
import org.example.models.Fixture;
import org.example.models.Match;
import org.example.models.TeamResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PremierLeagueFootballFixtureAdapterImpl implements FootballFixtureAdapter {

    private static final String PREMIER_LEAGUE = "https://footballapi.pulselive.com/football/fixtures?comps=1&teams=1,2,127,130,131,4,6,7,34,8,26,10,11,12,23,15,20,21,25,38&compSeasons=719&page=%d&pageSize=10&statuses=U,L,A,C&altIds=true&fast=false";

    private final TeamRepository teamRepository;

    public PremierLeagueFootballFixtureAdapterImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }
    @Override
    public Fixture getFootballFixture() throws IOException {
        int page = 0;
        int totalPages;
        Fixture fixtures = new Fixture(new HashMap<>());
        do {
            String url = String.format(PREMIER_LEAGUE, page);
            JsonNode rootNode = fetchJsonFromUrl(url);
            if (rootNode == null) break;

            totalPages = rootNode.path("pageInfo").path("numPages").asInt();
            parseFixtures(rootNode.path("content"), fixtures);

            page++;
        } while (page <= totalPages);

        return fixtures;
    }

    private JsonNode fetchJsonFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).ignoreContentType(true).get();
        String jsonResponse = doc.body().text();

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(jsonResponse);
    }

    private void parseFixtures(JsonNode contentNode,Fixture fixture) {
        for (JsonNode jsonMatch : contentNode) {
            JsonNode gameweek = jsonMatch.path("gameweek").path("gameweek");
            JsonNode teams = jsonMatch.path("teams");

            if (!gameweek.isMissingNode() && teams.isArray()) {
                String team1 = teams.get(0).path("team").path("name").asText();
                String team2 = teams.get(1).path("team").path("name").asText();

                JsonNode gol1Node = teams.get(0).path("score");
                JsonNode gol2Node = teams.get(1).path("score");

                Integer gol1 = null;
                Integer gol2 = null;

                if (!gol1Node.isMissingNode() && !gol2Node.isMissingNode()) {
                    gol1 = gol1Node.asInt();
                    gol2 = gol2Node.asInt();
                }

                TeamResult localTeam = new TeamResult(teamRepository.getOrCreateTeam(team1), gol1);
                TeamResult visitingTeam = new TeamResult(teamRepository.getOrCreateTeam(team2), gol2);

                Integer date = gameweek.asInt();

                Match match = new Match(localTeam, visitingTeam);
                fixture.getMatches().computeIfAbsent(date, k -> new ArrayList<>()).add(match);
            }
        }
    }
}
