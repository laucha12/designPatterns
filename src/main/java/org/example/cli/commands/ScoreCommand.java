package org.example.cli.commands;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "score",
        aliases = {"s"},
        description = "Get the score of a given fixture"
)
public class ScoreCommand implements Callable<Integer> {

    private static Terminal terminal;

    static {
        try {
            terminal = TerminalBuilder.terminal();
        } catch (IOException e) {
            System.err.println("Failed to initialize terminal: " + e.getMessage());
        }
    }

    private static void simulateProgress(String task) throws InterruptedException {
        System.out.println(task);
        for (int i = 0; i <= 100; i += 20) {
            System.out.print("\rProgress: " + i + "%");
            Thread.sleep(500);
        }
        System.out.println("\rProgress: 100% ✅");
    }

    private static void clearScreen() {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.flush();
    }

    @Override
    public Integer call() throws Exception {
        clearScreen();
        simulateProgress("Getting score...");
        return 1;
    }

}
