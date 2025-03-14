package org.example;

import org.example.adapters.PremierLeagueFootballFixtureAdapterImpl;
import org.example.file.EncryptionDecorator;
import org.example.file.FileFixtureDataSource;
import org.example.file.FixtureDataSource;
import org.example.file.SignatureDecorator;
import org.example.interfaces.FootballFixtureAdapter;
import org.example.interfaces.TeamRepository;
import org.example.models.Fixture;
import org.example.repositories.TeamRepositoryOnMemoryImpl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        TeamRepository teamRepository = TeamRepositoryOnMemoryImpl.getInstance();
        FootballFixtureAdapter footballFixtureAdapter = new PremierLeagueFootballFixtureAdapterImpl(teamRepository);
        FixtureDataSource fixtureDataSource = new FileFixtureDataSource();
        fixtureDataSource = new EncryptionDecorator(fixtureDataSource, "hola");
        fixtureDataSource = new SignatureDecorator(fixtureDataSource);
        try(OutputStream outputStream = new FileOutputStream("fixture.txt")) {
            fixtureDataSource.writeFixture(footballFixtureAdapter.getFootballFixture(),outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        try(InputStream inputStream = new FileInputStream("fixture.txt")) {
//            Fixture fixture = fixtureDataSource.readFixture(inputStream);
//            System.out.println(fixture);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println(footballFixtureAdapter.getFootballFixture().getMatches());
//        FixtureCli.main(args);
    }
}