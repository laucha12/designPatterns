package org.example.models;

import lombok.Getter;

@Getter
public class Team {
    private final String name;

    public Team(String name) {
        this.name = name.trim();
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name+
                "'}";
    }
}
