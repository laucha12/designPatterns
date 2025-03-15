package org.example.adapters;

import org.example.interfaces.FootballFixtureAdapter;
import org.example.interfaces.TeamRepository;
import org.example.models.Fixture;
import org.example.models.Match;
import org.example.models.MatchDate;
import org.example.models.TeamResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ArgentinianFootballFixtureAdapterImpl implements FootballFixtureAdapter {

    private final String ARGENTINIAN_LEAGUE = "https://info.afa.org.ar/deposito/html/v3/htmlCenter/data/deportes/futbol/primeraa/pages/es/fixture.html?h=dfMc-page-ec43917b-a0f4-4d06-a34c-d9069f6f4ce0";
    private final TeamRepository teamRepository;


    public ArgentinianFootballFixtureAdapterImpl(final TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }
    @Override
    public Fixture getFootballFixture() throws IOException {
        Document doc = Jsoup.connect(ARGENTINIAN_LEAGUE).get();
        Elements fechas = doc.select(".fecha");
        //We can use builder for Fixture and Match
        Fixture fixture = new Fixture();
        int i = 1;
        for (Element fecha : fechas) {
            MatchDate matchDate = new MatchDate(i);
            Elements partidos = fecha.select(".match");
            for (Element partido: partidos) {
                Element local = partido.select(".local").first();
                Element visitante = partido.select(".visitante").first();

                String equipoLocal = local.text();
                String equipoVisitante = visitante.text();
                String localGolText = equipoLocal.replaceAll("[^0-9]", "");
                String visitanteGolText = equipoVisitante.replaceAll("[^0-9]", "");

                Integer localGol = localGolText.isEmpty() ? null : Integer.parseInt(localGolText);
                Integer visitanteGol = visitanteGolText.isEmpty() ? null : Integer.parseInt(visitanteGolText);


                String nombreLocal = equipoLocal.replaceAll("[0-9]|' '", "");
                String nombreVisitante = equipoVisitante.replaceAll("[0-9]|' '", "");

                TeamResult localTeam = new TeamResult(teamRepository.getOrCreateTeam(nombreLocal), localGol);
                TeamResult visitanteTeam = new TeamResult(teamRepository.getOrCreateTeam(nombreVisitante), visitanteGol);
                Match match = new Match(localTeam, visitanteTeam);
                //Avoid unconfirmed matches
                if(!localTeam.getTeam().getName().equals("A Confirmar")){
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
