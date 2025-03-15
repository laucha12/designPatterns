package org.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@NoArgsConstructor
public class Fixture {

    //Use map to get the dates by id
    Map<Integer, MatchDate> matchDates = new HashMap<>();

    public void addMatch(int date, Match match) {
        matchDates.putIfAbsent(date, new MatchDate(date));
        matchDates.get(date).addMatch(match);
    }

    public void addMatchDate(MatchDate matchDate) {
        matchDates.put(matchDate.getNumber(), matchDate);
    }

    public MatchDate getMatchDate(int matchDateNumber){
        return matchDates.get(matchDateNumber);
    }

    public List<MatchDate> getMatchDates(){
        return matchDates.values().stream().toList();
    }

    @JsonIgnore
    //Return dates that have prending matches
    public List<MatchDate> getPendingDates(){
        return matchDates.values().stream().filter(MatchDate::hasPendingMatches).toList();
    }


    @Override
    public String toString() {
        return "Fixture{" +
                "matchDates=" + matchDates +
                '}';
    }
}
