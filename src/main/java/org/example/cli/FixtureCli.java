package org.example.cli;
import org.example.cli.commands.CreateCommand;
import org.example.cli.commands.ScoreCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "fixture",
        mixinStandardHelpOptions = true,
        description = "A cli tool for creating and calculating the score of a fixture",
        subcommands = {CreateCommand.class, ScoreCommand.class},
        version = "1.0"
)
public class FixtureCli implements Callable<Integer> {


    enum League {Arg, Eng}

    @CommandLine.Option(names = {"-l", "--league"}, description = "League (Arg or Eng)", required = true)
    private League league;

    //Run code common to all commands
    private void init(){
        System.out.println("Hello World!");
    }

    private int executionStrategy(CommandLine.ParseResult parseResult) {
        init();
        return (new CommandLine.RunLast()).execute(parseResult);
    }

    public static void main(String[] args) {
        FixtureCli fixtureCli = new FixtureCli();
        int exitCode = new CommandLine(fixtureCli)
                .setExecutionStrategy(fixtureCli::executionStrategy)
                .execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        CommandLine.usage(this, System.out);
        return 0;
    }

}

