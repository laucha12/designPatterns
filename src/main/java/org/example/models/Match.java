package org.example.models;

import lombok.Getter;

@Getter
public class Match {
    private Team local;
    private Team visitor;



    public Match(Team local, Team visitor) {
        this.local = local;
        this.visitor = visitor;
    }


    @Override
    public String toString() {
        return "Match{" +
                "local=" + local +
                ", visitor=" + visitor +
                '}';
    }
}
