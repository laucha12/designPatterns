package org.example.cli.commands;

import org.example.models.Fixture;
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

    private static void getTeamScores() throws IOException {
        // Save terminal state
        terminal.enterRawMode();

        BindingReader reader = new BindingReader(terminal.reader());
        KeyMap<String> keyMap = getStringKeyMap();

        List<Match> matches = List.of(new Match(new TeamResult(new Team("Team A"), null), new TeamResult(new Team("Team B"), null)),
                new Match(new TeamResult(new Team("Team C"), null), new TeamResult(new Team("Team D"), null)));

        String[] teams = {"Team A", "Team B", "Team C", "Team D"};
        int[][] scores = new int[teams.length/2][2];
        boolean[][] firstDigitAfterChange = new boolean[teams.length/2][2];

        // Initialize all firstDigitAfterChange flags to true for initial input
        for (int i = 0; i < teams.length/2; i++) {
            firstDigitAfterChange[i][0] = true;
            firstDigitAfterChange[i][1] = true;
        }

        try {
            for (int match = 0; match < teams.length/2; match++) {
                System.out.println("Progress: " + match*100.0/ (teams.length/2) + "%");
                int selectedScore = 0; // 0 for left team, 1 for right team
                boolean matchComplete = false;
                int previousSelectedScore = -1; // Track position changes

                while (!matchComplete) {
                    // Detect cursor position change
                    if (previousSelectedScore != selectedScore) {
                        firstDigitAfterChange[match][selectedScore] = true;
                        previousSelectedScore = selectedScore;
                    }

                    // Clear any existing content and move to start of line
                    System.out.print("\r\033[K");

                    // Build and print the match display with highlighting
                    StringBuilder display = new StringBuilder();
                    display.append(teams[match*2]).append(" ");

                    // Left score with highlighting if selected
                    if (selectedScore == 0) {
                        display.append("\033[47m\033[30m<").append(scores[match][0]).append(">\033[0m");
                    } else {
                        display.append("<").append(scores[match][0]).append(">");
                    }

                    display.append(" - ");

                    // Right score with highlighting if selected
                    if (selectedScore == 1) {
                        display.append("\033[47m\033[30m<").append(scores[match][1]).append(">\033[0m");
                    } else {
                        display.append("<").append(scores[match][1]).append(">");
                    }

                    display.append(" ").append(teams[match*2 + 1]);

                    System.out.print(display.toString());
                    terminal.flush();

                    // Read key input
                    String operation = reader.readBinding(keyMap);

                    if (operation == null) {
                        // Unknown key - ignore
                        continue;
                    }

                    switch (operation) {
                        case "left":
                            selectedScore = 0;
                            break;
                        case "right":
                            selectedScore = 1;
                            break;
                        case "enter":
                            if (selectedScore == 0) {
                                selectedScore = 1;
                            } else {
                                matchComplete = true;
                            }
                            break;
                        case "backspace":
                            scores[match][selectedScore] /= 10; // Remove last digit
                            firstDigitAfterChange[match][selectedScore] = scores[match][selectedScore] == 0;
                            break;
                        case "delete":
                            scores[match][selectedScore] /= 10; // Remove last digit
                            firstDigitAfterChange[match][selectedScore] = scores[match][selectedScore] == 0;
                            break;
                        default:
                            // Check if it's a digit
                            if (operation.length() == 1 && Character.isDigit(operation.charAt(0))) {
                                int digit = Integer.parseInt(operation);

                                // If this is the first digit after changing position, replace the existing score
                                if (firstDigitAfterChange[match][selectedScore]) {
                                    scores[match][selectedScore] = digit;
                                    firstDigitAfterChange[match][selectedScore] = false;
                                } else {
                                    // Otherwise append to the existing score
                                    scores[match][selectedScore] = scores[match][selectedScore] * 10 + digit;
                                }
                            }
                            break;
                    }
                }

                // Reset for the next match
                firstDigitAfterChange[match][0] = true;
                firstDigitAfterChange[match][1] = true;

                // Print final match result before moving to next match
                System.out.print("\r\033[K");  // Clear line
                System.out.printf("Match %d result: %s %d - %d %s\n\n",
                        match + 1, teams[match*2], scores[match][0],
                        scores[match][1], teams[match*2 + 1]);
                clearScreen();
            }

            System.out.println("All scores have been entered!");

        } finally {
            // Restore terminal to normal mode
            terminal.setAttributes(terminal.getAttributes());
        }
    }

    @Override
    public Integer call() throws Exception {
        clearScreen();
//        simulateProgress("Creating a fixture...");
        getTeamScores();
        return 0;
    }
}