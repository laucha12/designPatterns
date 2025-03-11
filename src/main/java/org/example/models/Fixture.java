package org.example.models;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class Fixture {

    // It is a map because the key the number of the date(Fecha) and the value is a list of matches
    private final Map<Integer,List<Match>> matches;

    public Fixture(Map<Integer, List<Match>> matches) {
        this.matches = matches;
    }
}
