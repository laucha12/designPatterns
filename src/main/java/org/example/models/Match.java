package org.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.interfaces.Cloneable;

@Getter
@NoArgsConstructor
public class Match implements Cloneable<Match> {

    private boolean played;
    private TeamResult localResult;
    private TeamResult visitorResult;


    public Match(TeamResult localResult, TeamResult visitorResult) {
        this.localResult = localResult;
        this.visitorResult = visitorResult;
        played = localResult.getGoals() != null && visitorResult.getGoals() != null;
    }


    @Override
    public Match clone() {
        return new Match(localResult.clone(), visitorResult.clone());
    }

    @JsonIgnore
    public boolean isPending(){
        return !played;
    }


    @Override
    public String toString() {
        return "Match{" +
                "played=" + played +
                ", localResult=" + localResult +
                ", visitorResult=" + visitorResult +
                '}';
    }
}
