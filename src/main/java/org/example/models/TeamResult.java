package org.example.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.interfaces.Cloneable;

@Getter
@NoArgsConstructor
public class TeamResult implements Cloneable<TeamResult> {

    private Team team;

    @Setter
    private Integer goals;

    public TeamResult(Team team, Integer goals) {
        this.team = team;
        this.goals = goals;
    }

    @Override
    public TeamResult clone() {
        return new TeamResult(team.clone(), goals);
    }

    @Override
    public String toString() {
        return "TeamResult{" +
                "team=" + team +
                ", goals=" + goals +
                '}';
    }
}
