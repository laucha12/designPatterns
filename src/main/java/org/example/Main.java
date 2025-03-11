package org.example;

import org.example.adapters.FootballFixtureAdapterImpl;
import org.example.interfaces.FootballFixtureAdapter;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        FootballFixtureAdapter footballFixtureAdapter = new FootballFixtureAdapterImpl();
        System.out.println(footballFixtureAdapter.getFootballFixture().getMatches());
    }
}