package org.example.repositories;

import org.example.interfaces.TeamRepository;
import org.example.models.Team;

import java.util.HashMap;
import java.util.Map;

public class TeamRepositoryOnMemoryImpl implements TeamRepository {

    private final Map<String, Team> teams;

    private static TeamRepositoryOnMemoryImpl instance;

    private TeamRepositoryOnMemoryImpl() {
        this.teams = new HashMap<>();
    }

    public static TeamRepositoryOnMemoryImpl getInstance() {
        if (instance == null) {
            instance = new TeamRepositoryOnMemoryImpl();
        }
        return instance;
    }

    @Override
    public Team getOrCreateTeam(String name) {
        return teams.computeIfAbsent(name, Team::new);
    }
}
