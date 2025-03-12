package org.example.models;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TeamResult {

    private final Team team;

    @Setter
    private Integer goals;

    public TeamResult(Team team, Integer goals) {
        this.team = team;
        this.goals = goals;
    }

    @Override
    public String toString() {
        return "TeamResult{" +
                "team=" + team +
                ", goals=" + goals +
                '}';
    }
}
