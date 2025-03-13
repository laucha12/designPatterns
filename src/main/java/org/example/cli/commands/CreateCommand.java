package org.example.cli.commands;

import org.example.models.Match;
import org.example.models.Team;
import org.example.models.TeamResult;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import picocli.CommandLine;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "create",
        aliases = {"c"},
        description = "Create a fixture for a league"
)
public class CreateCommand implements Callable<Integer> {

    private static Terminal terminal;

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

    static {
        try {
            terminal = TerminalBuilder.builder()
                    .system(true)
                    .jansi(true)
                    .build();
        } catch (IOException e) {
            System.err.println("Failed to initialize terminal: " + e.getMessage());
        }
    }

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

    private static void getTeamScores(){
        // Save terminal state
        terminal.enterRawMode();

        BindingReader reader = new BindingReader(terminal.reader());
        KeyMap<String> keyMap = getStringKeyMap();

        List<Match> matches = List.of(new Match(new TeamResult(new Team("Team A"), null), new TeamResult(new Team("Team B"), null)),
                new Match(new TeamResult(new Team("Team C"), null), new TeamResult(new Team("Team D"), null)));
        int[] scores = new int[2];

        //Save if the digit is being entered after switching score cursor to delete previous score
        //Is true if the digit being entered is the first after moving cursor
        boolean[] firstDigitAfterChange = new boolean[2];
        Arrays.fill(firstDigitAfterChange, true);

        try {
            for (int matchIndex = 0; matchIndex < matches.size(); matchIndex++) {
                final Match match = matches.get(matchIndex);
                System.out.println("Progress: " + matchIndex*100.0/ matches.size() + "%");
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
                    display.append(match.getLocalResult().getTeam().getName()).append(" ");

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

                    display.append(" ").append(match.getVisitorResult().getTeam().getName());

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
            }

            System.out.println("All scores have been entered!");
            System.out.println(matches);

        } finally {
            // Restore terminal to normal mode
            terminal.setAttributes(terminal.getAttributes());
        }
    }

    @Override
    public Integer call(){
        clearScreen();
        getTeamScores();
        return 0;
    }
}