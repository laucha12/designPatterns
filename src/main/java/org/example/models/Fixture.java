package org.example.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class Fixture {

    // It is a map because the key the number of the date(Fecha) and the value is a list of matches
    private Map<Integer,List<Match>> matches = new HashMap<>();

    public Fixture(Map<Integer, List<Match>> matches) {
        this.matches = matches;
    }

    @Override
    public String toString() {
        return "Fixture{" +
                "matches=" + matches +
                '}';
    }
}
