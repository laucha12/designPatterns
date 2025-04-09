package org.example.adapters;

import org.example.interfaces.FootballFixtureAdapter;
import org.example.interfaces.TeamRepository;
import org.example.models.Fixture;
import org.example.models.Match;
import org.example.models.MatchBuilder;
import org.example.models.MatchDate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Optional;

public class ArgentinianFootballFixtureAdapterImpl implements FootballFixtureAdapter {

    private  String ARGENTINIAN_LEAGUE = "https://infaao.afa.org.ar/deposito/html/v3/htmlCenter/data/deportes/futbol/primeraa/pages/es/fixture.html?h=dfMc-page-ec43917b-a0f4-4d06-a34c-d9069f6f4ce0";
    private int counter = 1;
    private final TeamRepository teamRepository;


    public ArgentinianFootballFixtureAdapterImpl(final TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    private String getTeamName(final String teamText){
        return teamText.replaceAll("[0-9]|' '", "");
    }

    private String getTeamGoals(final String teamText){
        return teamText.replaceAll("[^0-9]", "");
    }

    @Override
    public Fixture getFootballFixture() throws IOException {
        counter++;
        if (counter > 10)
            ARGENTINIAN_LEAGUE = "https://info.afa.org.ar/deposito/html/v3/htmlCenter/data/deportes/futbol/primeraa/pages/es/fixture.html?h=dfMc-page-ec43917b-a0f4-4d06-a34c-d9069f6f4ce0";

        Document doc = Jsoup.connect(ARGENTINIAN_LEAGUE).get();
        Elements fechas = doc.select(".fecha");
        //We can use builder for Fixture and Match
        Fixture fixture = new Fixture();
        int i = 1;
        for (Element fecha : fechas) {
            MatchDate matchDate = new MatchDate(i);
            Elements partidos = fecha.select(".match");
            for (Element partido: partidos) {
                MatchBuilder matchBuilder = Match.builder();
                Optional<String> localTeamText = Optional.ofNullable(partido.select(".local").first())
                        .map(Element::text);
                Optional<String> visitorTeamText = Optional.ofNullable(partido.select(".visitante").first())
                        .map(Element::text);

                Optional<String> referee = Optional.ofNullable(partido.select(".arbitro").first())
                        .map(Element::text)
                        .map(s -> s.replaceAll("Árbitro: ", " "))
                        .filter(s -> !s.isEmpty());

                //Set local team
                localTeamText.map(this::getTeamName)
                        .map(teamRepository::getOrCreateTeam)
                        .ifPresent(matchBuilder::localTeam);
                //Set visitor team
                visitorTeamText.map(this::getTeamName)
                        .map(teamRepository::getOrCreateTeam)
                        .ifPresent(matchBuilder::visitorTeam);

                //Set local goals
                localTeamText.map(this::getTeamGoals)
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .ifPresent(matchBuilder::localGoals);
                //Set visitor goals
                visitorTeamText.map(this::getTeamGoals)
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .ifPresent(matchBuilder::visitorGoals);

                //Set referee
                referee.ifPresent(matchBuilder::referee);

                Match match = matchBuilder.build();
                //Avoid unconfirmed matches
                if(!match.getLocalTeam().getName().equals("A Confirmar")){
                    matchDate.addMatch(match);
                }
            }
            //Avoid empty dates
            if(!matchDate.getMatches().isEmpty()) {
                fixture.addMatchDate(matchDate);
            }
            i++;
        }
        return fixture;
    }
}
