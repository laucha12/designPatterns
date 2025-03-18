package org.example.cli.commands;

import org.example.cli.FixtureCli;
import org.example.models.*;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import picocli.CommandLine;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "create",
        aliases = {"c"},
        description = "Create a fixture for a league"
)
public class CreateCommand implements Callable<Integer> {

    @CommandLine.ParentCommand
    private FixtureCli parent;

    private static final Terminal terminal = FixtureCli.getTerminal();

    // Define key codes - with additional codes for Mac
    private static final String KEY_LEFT = "\033[D";
    private static final String KEY_RIGHT = "\033[C";
    private static final String KEY_ENTER = "\r";
    private static final String KEY_ENTER_ALT = "\n";  // Alternative Enter key (LF)
    private static final String KEY_BACKSPACE = "\177";
    private static final String KEY_DELETE = "\033[3~";
    // Mac-specific arrow keys (some terminals)
    private static final String KEY_LEFT_MAC = "\033OD";
    private static final String KEY_RIGHT_MAC = "\033OC";

    private static void clearScreen() {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.flush();
    }


    private static KeyMap<String> getStringKeyMap() {
        KeyMap<String> keyMap = new KeyMap<>();

        // Add both standard and Mac-specific key bindings
        keyMap.bind("left", KEY_LEFT, KEY_LEFT_MAC);
        keyMap.bind("right", KEY_RIGHT, KEY_RIGHT_MAC);
        keyMap.bind("enter", KEY_ENTER, KEY_ENTER_ALT);
        keyMap.bind("backspace", KEY_BACKSPACE);
        keyMap.bind("delete", KEY_DELETE);

        // Add numeric keymap entries (0-9)
        for (int i = 0; i <= 9; i++) {
            String digit = String.valueOf(i);
            keyMap.bind(digit, digit);
        }

        return keyMap;
    }

    private static String highlight(String input) {
        return String.format("\033[47m\033[30m%s\033[0m",input);
    }

    private Match getMatchScore(Match fixtureMatch, BindingReader reader, KeyMap<String> keyMap) {

        //Get new instance of match to change the results
        Match match = fixtureMatch.clone();
        int[] scores = new int[2];
        //Save if the digit is being entered after switching score cursor to delete previous score
        //Is true if the digit being entered is the first after moving cursor
        boolean[] firstDigitAfterChange = new boolean[2];
        Arrays.fill(firstDigitAfterChange, true);


        int selectedScore = 0; // 0 for left team, 1 for right team
        boolean matchComplete = false;
        int previousSelectedScore = -1; // Track position changes

        while (!matchComplete) {
            // Detect cursor position change
            if (previousSelectedScore != selectedScore) {
                firstDigitAfterChange[selectedScore] = true;
                previousSelectedScore = selectedScore;
            }
            // Clear any existing content and move to start of line
            System.out.print("\r\033[K");

            StringBuilder display = new StringBuilder();
            //Add first team name
            display.append(fixtureMatch.getLocalResult().getTeam().getName()).append(" ");

            // Left score with highlighting if selected
            if (selectedScore == 0) {
                display.append(highlight(String.valueOf(scores[0])));
            } else {
                display.append(scores[0]);
            }

            display.append(" - ");

            // Right score with highlighting if selected
            if (selectedScore == 1) {
                display.append(highlight(String.valueOf(scores[1])));
            } else {
                display.append(scores[1]);
            }

            display.append(" ").append(fixtureMatch.getVisitorResult().getTeam().getName());

            System.out.print(display);
            terminal.flush();

            // Read key input
            String operation = reader.readBinding(keyMap);

            if (operation == null) {
                // Unknown key - ignore
                continue;
            }
            //Switch expression
            switch (operation) {
                case "left" -> selectedScore = 0;
                case "right" -> selectedScore = 1;
                case "enter" -> {
                    if (selectedScore == 0) {
                        selectedScore = 1;
                    } else {
                        matchComplete = true;
                    }
                }
                case "backspace", "delete" -> {
                    scores[selectedScore] /= 10;// Remove last digit
                    firstDigitAfterChange[selectedScore] = (scores[selectedScore] == 0);
                }
                default -> {
                    // Check if it's a digit
                    if (operation.length() == 1 && Character.isDigit(operation.charAt(0))) {
                        int digit = Integer.parseInt(operation);

                        // If this is the first digit after changing position, replace the existing score
                        if (firstDigitAfterChange[selectedScore]) {
                            scores[selectedScore] = digit;
                            firstDigitAfterChange[selectedScore] = false;
                        } else {
                            // Otherwise append to the existing score
                            scores[selectedScore] = scores[selectedScore] * 10 + digit;
                        }
                    }
                }
            }
        }

        // Reset for the next match
        match.getLocalResult().setGoals(scores[0]);
        match.getVisitorResult().setGoals(scores[1]);
        Arrays.fill(firstDigitAfterChange, true);
        Arrays.fill(scores,0);
        clearScreen();
        return match;
    }

    private Fixture getTeamScores(){
        // Save terminal state
        terminal.enterRawMode();

        BindingReader reader = new BindingReader(terminal.reader());
        KeyMap<String> keyMap = getStringKeyMap();

        Fixture leagueFixture = parent.getFixture();
        Fixture userFixture = new Fixture();


        List<MatchDate> pendingDates = leagueFixture.getPendingDates();
        try {
            for(int date = 0; date < pendingDates.size(); date ++ ){
                MatchDate matchDate = pendingDates.get(date);
                for(Match match : matchDate.getPendingMatches()){
                    System.out.printf("Progress: %.2f %%\n",date*100.0/ pendingDates.size());
                    Match userMatch = getMatchScore(match, reader, keyMap);
                    userFixture.addMatch(matchDate.getNumber(), userMatch);
                }

            }
        } finally {
            // Restore terminal to normal mode
            terminal.setAttributes(terminal.getAttributes());
        }

        return userFixture;

    }

    private void writeFixture(Fixture fixture) {
        try (OutputStream outputStream = new FileOutputStream(parent.getFileName())) {
            parent.getFixtureDataSource().writeFixture(fixture, outputStream);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer call(){
        clearScreen();
        Fixture userFixture = getTeamScores();
        writeFixture(userFixture);
        return 0;
    }
}