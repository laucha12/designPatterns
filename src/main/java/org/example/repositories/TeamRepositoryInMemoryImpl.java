package org.example.repositories;

import org.example.interfaces.TeamRepository;
import org.example.models.Team;

import java.util.HashMap;
import java.util.Map;

public class TeamRepositoryInMemoryImpl implements TeamRepository {


    private final Map<String, Team> teams;

    private static TeamRepositoryInMemoryImpl instance;

    private TeamRepositoryInMemoryImpl() {
        this.teams = new HashMap<>();
    }

    public static TeamRepositoryInMemoryImpl getInstance() {
        if (instance == null) {
            instance = new TeamRepositoryInMemoryImpl();
        }
        return instance;
    }

    @Override
    public Team getOrCreateTeam(String name) {
        return teams.computeIfAbsent(name, Team::new);
    }
}
