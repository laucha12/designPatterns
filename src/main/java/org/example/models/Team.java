package org.example.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.interfaces.Cloneable;

@Getter
@NoArgsConstructor
public class Team implements Cloneable<Team> {
    private String name;

    public Team(String name) {
        this.name = name.trim();
    }

    @Override
    public Team clone() {
        //Teams are immutable
        return this;
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name+
                "'}";
    }
}
