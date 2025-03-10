package org.example.models;

import lombok.Getter;

@Getter
public class Team {
    private String name;
    private Integer goals;

    public Team(String name, Integer goals) {
        this.name = name;
        this.goals = goals;
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + " }";
    }
}
