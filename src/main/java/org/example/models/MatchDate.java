package org.example.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class MatchDate {

    private int number;
    //Only because Set does not allow a get method (?)
    private Map<String ,Match> matches;

    public MatchDate(int number) {
        this.number = number;
        matches = new HashMap<>();
    }

    public void addMatch(Match match) {
        matches.put(match.getTeamsString(),match);
    }

    public Match getMatch(String teamsString){
        return matches.get(teamsString);
    }

    @JsonIgnore
    public Set<Match> getPlayedMatches() {
        return matches
                .values()
                .stream()
                .filter(Match::isPlayed)
                .collect(Collectors.toSet());
    }

    @JsonIgnore
    public Set<Match> getPendingMatches() {
        return matches
                .values()
                .stream()
                .filter(Match::isPending)
                .collect(Collectors.toSet());
    }

    @JsonIgnore
    public boolean hasPendingMatches() {
        return !getPendingMatches().isEmpty();
    }

    @JsonIgnore
    public boolean isPlayed() {
        return !hasPendingMatches();
    }

    @Override
    public String toString() {
        return "MatchDay{" +
                "day=" + number +
                ", matches=" + matches +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MatchDate matchDate)) return false;
        return number == matchDate.number;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }
}
