package org.example;

import org.example.adapters.PremierLeagueFootballFixtureAdapterImpl;
import org.example.cli.FixtureCli;
import org.example.file.FileFixtureDataSource;
import org.example.file.FixtureDataSource;
import org.example.interfaces.FootballFixtureAdapter;
import org.example.interfaces.TeamRepository;
import org.example.models.Fixture;
import org.example.repositories.TeamRepositoryOnMemoryImpl;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        TeamRepository teamRepository = TeamRepositoryOnMemoryImpl.getInstance();
        FootballFixtureAdapter footballFixtureAdapter = new PremierLeagueFootballFixtureAdapterImpl(teamRepository);
        FixtureDataSource fixtureDataSource = new FileFixtureDataSource();
//        try(OutputStream outputStream = new FileOutputStream("fixture.dat")) {
//            fixtureDataSource.writeFixture(footballFixtureAdapter.getFootballFixture(),outputStream);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        try(InputStream inputStream = new FileInputStream("fixture.dat")) {
            Fixture fixture = fixtureDataSource.readFixture(inputStream);
            System.out.println(fixture);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        System.out.println(footballFixtureAdapter.getFootballFixture().getMatches());
//        FixtureCli.main(args);
    }
}