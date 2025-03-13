package org.example.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class TeamResult {

    private Team team;

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
