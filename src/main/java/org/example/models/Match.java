package org.example.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Match {

    private TeamResult localResult;
    private TeamResult visitorResult;



    public Match(TeamResult localResult, TeamResult visitorResult) {
        this.localResult = localResult;
        this.visitorResult = visitorResult;
    }


    @Override
    public String toString() {
        return "Match{" +
                "local=" + localResult +
                ", visitorResult=" + visitorResult +
                '}';
    }
}
