package org.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Getter
@NoArgsConstructor
@ToString
public class Match {

    private boolean played;
    private Team localTeam;
    private Team visitorTeam;
    private Optional<String> referee;
    private Optional<Integer> localGoals;
    private Optional<Integer> visitorGoals;


    public Match(Team localTeam, Team visitorTeam, Optional<Integer> localGoals, Optional<Integer> visitorGoals,Optional<String> referee) {
        this.played = localGoals.isPresent() && visitorGoals.isPresent();
        this.localTeam = localTeam;
        this.visitorTeam = visitorTeam;
        this.localGoals = localGoals;
        this.visitorGoals = visitorGoals;
        this.referee = referee;
    }

    @JsonIgnore
    public boolean isPending(){
        return !played;
    }



    public static MatchBuilder builder(){
        return new Builder();
    }

    public static class Builder implements MatchBuilder{
        private Team localTeam;
        private Team visitorTeam;
        private Integer localGoals;
        private Integer visitorGoals;
        private String referee;

        @Override
        public MatchBuilder localTeam(Team localTeam) {
            this.localTeam = localTeam;
            return this;
        }

        @Override
        public MatchBuilder visitorTeam(Team visitorTeam) {
            this.visitorTeam = visitorTeam;
            return this;
        }

        @Override
        public MatchBuilder localGoals(Integer goals) {
            this.localGoals = goals;
            return this;
        }

        @Override
        public MatchBuilder visitorGoals(Integer visitorGoals) {
            this.visitorGoals = visitorGoals;
            return this;
        }

        @Override
        public MatchBuilder referee(String referee) {
            this.referee = referee;
            return this;
        }


        @Override
        public Match build() {
            return new Match(localTeam, visitorTeam, Optional.ofNullable(localGoals), Optional.ofNullable(visitorGoals),Optional.ofNullable(referee));
        }
    }
}
