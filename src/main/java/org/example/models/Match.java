package org.example.models;

import lombok.Getter;

@Getter
public class Match {

    private final TeamResult localResult;
    private final TeamResult visitorResult;



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
