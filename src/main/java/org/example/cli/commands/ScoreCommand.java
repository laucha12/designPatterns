package org.example.cli.commands;

import org.example.cli.FixtureCli;
import org.example.file.FixtureDataSource;
import org.example.models.Fixture;
import org.example.models.Match;
import org.example.models.MatchDate;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import picocli.CommandLine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "score",
        aliases = {"s"},
        description = "Get the score of a given fixture"
)
public class ScoreCommand implements Callable<Integer> {

    @CommandLine.ParentCommand
    private FixtureCli  parent;

    private static final Terminal terminal = FixtureCli.getTerminal();

    private Fixture readFixture(final String fileName, FixtureDataSource dataSource){
        try(InputStream inputStream = new FileInputStream(fileName)) {
            return dataSource.readFixture(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getScore(final Fixture leagueFixture, final Fixture userFixture) {
        int score = 0;
        //get all the dates the user predicted (they will be marked as pending because of when they were stored)
        for(MatchDate userDate : userFixture.getMatchDatesList()){
            //Get the results for that date
            final MatchDate leagueDate = leagueFixture.getMatchDate(userDate.getNumber());
            //We add points for all played matches
            for(Match userPrediction: userDate.getPlayedMatches()){
                Match leagueResult = leagueDate.getMatch(userPrediction.getLocalResult().getTeam(), userPrediction.getVisitorResult().getTeam());
                if(leagueResult != null && leagueResult.isPlayed() &&
                    Objects.equals(userPrediction.getLocalResult().getGoals(), leagueResult.getLocalResult().getGoals()) &&
                    Objects.equals(userPrediction.getVisitorResult().getGoals(), leagueResult.getVisitorResult().getGoals())){
                    score++;
                }
//                score += Optional.ofNullable(userDate.getMatch(playedMatch.getTeamsString()))//user has a prediction for that match
//                        .filter(m -> Objects.equals(m.getLocalResult().getGoals(), playedMatch.getLocalResult().getGoals()) && Objects.equals(m.getVisitorResult().getGoals(), playedMatch.getVisitorResult().getGoals())) //that prediction is the same
//                        .map(m -> 1)
//                        .orElse(0);
            }
        }
        return score;
    }

    private static void clearScreen() {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.flush();
    }

    @Override
    public Integer call() throws Exception {
        clearScreen();
        final Fixture leagueFixture = parent.getFixture();
        final Fixture userFixture = readFixture(parent.getFileName(), parent.getFixtureDataSource());
        System.out.printf("Prode score is %d\n", getScore(leagueFixture, userFixture));
        return 0;
    }

}
