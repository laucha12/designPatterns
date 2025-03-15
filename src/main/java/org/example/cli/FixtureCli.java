package org.example.cli;
import lombok.Getter;
import org.example.adapters.ArgentinianFootballFixtureAdapterImpl;
import org.example.adapters.PremierLeagueFootballFixtureAdapterImpl;
import org.example.cli.commands.CreateCommand;
import org.example.cli.commands.ScoreCommand;
import org.example.file.EncryptionDecorator;
import org.example.file.FileFixtureDataSource;
import org.example.file.FixtureDataSource;
import org.example.file.SignatureDecorator;
import org.example.interfaces.TeamRepository;
import org.example.models.Fixture;
import org.example.repositories.TeamRepositoryOnMemoryImpl;
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

    @CommandLine.Option(names = {"-s", "--signed"}, description = "Sign the fixture for future validation")
    private boolean signed;

    @CommandLine.Option(names = {"-p", "--password"}, description = "password used to encrypt and decrypt the fixture", interactive = true)
    private String password;

    @CommandLine.Option(names = {"-f", "--file"}, description = "The file where to save or load the fixture", defaultValue = "fixture.txt")
    @Getter
    private String fileName;

    @Getter
    private Fixture fixture;

    @Getter
    private FixtureDataSource fixtureDataSource;

    //Run code common to all commands
    private void init() throws Exception{
        TeamRepository teamRepository = TeamRepositoryOnMemoryImpl.getInstance();
        fixture = switch (league){
            case Arg -> new ArgentinianFootballFixtureAdapterImpl(teamRepository).getFootballFixture();
            case Eng -> new PremierLeagueFootballFixtureAdapterImpl(teamRepository).getFootballFixture();
        };
        fixtureDataSource = new FileFixtureDataSource();
        //Maybe use factory?
        if(password != null){
            fixtureDataSource = new EncryptionDecorator(fixtureDataSource, password);
        }
        if(signed) {
            fixtureDataSource = new SignatureDecorator(fixtureDataSource);
        }
    }

    private int executionStrategy(CommandLine.ParseResult parseResult) {
        try {
            init();
        }catch (Exception e){
            return 1;//Exit with error code
        }
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

