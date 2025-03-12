package org.example;

import org.example.adapters.FootballFixtureAdapterImpl;
import org.example.interfaces.FootballFixtureAdapter;
import org.example.interfaces.TeamRepository;
import org.example.repositories.TeamRepositoryOnMemoryImpl;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        TeamRepository teamRepository = TeamRepositoryOnMemoryImpl.getInstance();
        FootballFixtureAdapter footballFixtureAdapter = new FootballFixtureAdapterImpl(teamRepository);
        System.out.println(footballFixtureAdapter.getFootballFixture().getMatches());
    }
}