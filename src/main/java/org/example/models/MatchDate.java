package org.example.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class MatchDate {

    private int number;
    private Set<Match> matches;

    public MatchDate(int number) {
        this.number = number;
        matches = new HashSet<>();
    }

    public void addMatch(Match match) {
        matches.add(match);
    }

    @JsonIgnore
    public Set<Match> getPlayedMatches() {
        return matches
                .stream()
                .filter(Match::isPlayed)
                .collect(Collectors.toSet());
    }

    @JsonIgnore
    public Set<Match> getPendingMatches() {
        return matches
                .stream()
                .filter(Match::isPending)
                .collect(Collectors.toSet());
    }

    public boolean hasPendingMatches() {
        return !getPendingMatches().isEmpty();
    }

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
