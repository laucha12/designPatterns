package org.example.interfaces;

import org.example.models.Fixture;

import java.io.IOException;

public interface FootballFixtureAdapter {
    Fixture getFootballFixture() throws IOException;
}
