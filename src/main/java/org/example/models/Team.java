package org.example.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Team{
    private String name;

    public Team(String name) {
        this.name = name.trim();
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name+
                "'}";
    }
}
