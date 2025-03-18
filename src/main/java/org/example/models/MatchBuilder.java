package org.example.models;

public interface MatchBuilder {
    MatchBuilder localTeam(Team localTeam);
    MatchBuilder visitorTeam(Team visitorTeam);
    MatchBuilder localGoals(Integer goals);
    MatchBuilder visitorGoals(Integer visitorGoals);
    Match build();
}
