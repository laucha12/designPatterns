package org.example.interfaces;

import org.example.models.Team;

public interface TeamRepository {
    Team getOrCreateTeam(String name);
}
