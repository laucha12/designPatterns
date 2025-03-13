package org.example;

import org.example.cli.FixtureCli;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
//        TeamRepository teamRepository = TeamRepositoryOnMemoryImpl.getInstance();
//        FootballFixtureAdapter footballFixtureAdapter = new PremierLeagueFootballFixtureAdapterImpl(teamRepository);
//        System.out.println(footballFixtureAdapter.getFootballFixture().getMatches());
        FixtureCli.main(args);
    }
}