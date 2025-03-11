package org.example.adapters;

import org.example.interfaces.FootballFixtureAdapter;
import org.example.models.Fixture;
import org.example.models.Match;
import org.example.models.Team;
import org.example.models.TeamResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FootballFixtureAdapterImpl implements FootballFixtureAdapter {
    private final String ARGENTINIAN_LEAGUE = "https://info.afa.org.ar/deposito/html/v3/htmlCenter/data/deportes/futbol/primeraa/pages/es/fixture.html?h=dfMc-page-ec43917b-a0f4-4d06-a34c-d9069f6f4ce0";

    @Override
    public Fixture getFootballFixture() throws IOException {
        Document doc = Jsoup.connect(ARGENTINIAN_LEAGUE).get();
        Elements fechas = doc.select(".fecha");
        Map<Integer,List<Match>> fixture = new HashMap<>();
        int i = 1;
        for (Element fecha : fechas) {
            fixture.put(i, new ArrayList<>());
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

                TeamResult localTeam = new TeamResult(new Team(nombreLocal), localGol);
                TeamResult visitanteTeam = new TeamResult(new Team(nombreVisitante), visitanteGol);
                Match match = new Match(localTeam, visitanteTeam);
                fixture.get(i).add(match);
            }
            i++;
        }
        return new Fixture(fixture);
    }
}
