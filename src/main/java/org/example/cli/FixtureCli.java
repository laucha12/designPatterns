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
import org.example.interfaces.FootballFixtureAdapter;
import org.example.interfaces.TeamRepository;
import org.example.models.Fixture;
import org.example.proxies.CircuitBreakerProxy;
import org.example.repositories.TeamRepositoryOnMemoryImpl;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "fixture",
        mixinStandardHelpOptions = true,
        description = "A cli tool for creating and calculating the score of a prode",
        subcommands = {CreateCommand.class, ScoreCommand.class},
        version = "1.0"
)
public class FixtureCli implements Callable<Integer> {

    @Getter
    private static Terminal terminal;

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

    enum League {Arg, Pre}

    @CommandLine.Option(names = {"-l", "--league"}, description = "League (Arg or Pre)", required = true)
    private League league;

    @CommandLine.Option(names = {"-s", "--signed"}, description = "Sign the prode for future validation")
    private boolean signed;

    @CommandLine.Option(names = {"-p", "--password"}, description = "password used to encrypt and decrypt the prode")
    private String password;

    @CommandLine.Option(names = {"-f", "--file"}, description = "The file where to save or load the prode", defaultValue = "fixture.txt")
    @Getter
    private String fileName;

    @Getter
    private Fixture fixture;

    @Getter
    private FixtureDataSource fixtureDataSource;

    //Run code common to all commands
    private void init() throws Exception{
        TeamRepository teamRepository = TeamRepositoryOnMemoryImpl.getInstance();
        FootballFixtureAdapter adapter = null;
        int attempts = 0;
        adapter = switch (league){
            case Arg -> (FootballFixtureAdapter) CircuitBreakerProxy.newInstance(new ArgentinianFootballFixtureAdapterImpl(teamRepository));
            case Pre ->  ((FootballFixtureAdapter) CircuitBreakerProxy.newInstance(new PremierLeagueFootballFixtureAdapterImpl(teamRepository)));
        };
        Fixture toReturn = null;
        while(attempts < 10000 && toReturn == null){
            attempts++;
            toReturn = adapter.getFootballFixture();
        }
        if(toReturn == null){
            throw new Exception("Failed to get fixture");
        }
        this.fixture = toReturn;
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
        //Run help and version without init
        if(parseResult.isUsageHelpRequested()){
            return this.call();
        }
        if(parseResult.isVersionHelpRequested()){
            new CommandLine(this).printVersionHelp(System.out);
            return 0;
        }
        try {
            init();
        }catch (Exception e){
            e.printStackTrace();
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

