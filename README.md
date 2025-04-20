# ⚽️ Prode
This project implements a _Prode_, a popular argentinain game where a person tries to predict the results of the 
fixture of a football league, and gains a point for each correct prediction. A prediction is considered correct when 
the exact result of the match is predicted, not only the winner of the match. 

The repository for this project can be found [here](https://github.com/laucha12/designPatterns).

> [!WARNING]  
> This project is not intended to be used for gambling purposes. It was made because it's a popular argentinian game 
intended to be played with families and friends. 

## Table of contents 
- [Requirements](#requirements)
- [Compilation](#compilation)
- [Execution](#execution)
- [About](#about)


## Requirements
The following is needed to execute the project.
- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Apache Maven](https://maven.apache.org/install.html)

## Compilation
To compile the project, the following steps must be followed:
1. Clone the repository
    ```bash
    git clone https://github.com/laucha12/designPatterns.git
   ```
2. Move to the project directory
    ```bash
    cd designPatterns
   ```
3. Compile and install the project
    ```bash
   mvn clean install
    ```

## Configuration
Because LLMs are used to get a team description, an API key should be provided for the pogram.

This key should be defined as an environment variable, and it can be generated in the [OpenAI platform](https://platform.openai.com/docs/overview) 
or [Google AI studio](https://aistudio.google.com). A template for the names of the variables is in the `.env_template` file, and 
the following command can be used to load them:
```bash
export $(grep -v '^#' .env | xargs)
```

## Execution
To get a description of the commands and the possible options, execute
```bash
java -jar target/DesignPatterns-1.0-SNAPSHOT-jar-with-dependencies.jar
```
### Create a fixture
The `create` or `c` command is used to create a prode from the fixture of a 
given league. The following options can be passed:
- `-f, --file=<fileName>` The file where the prode will be saved. The default is `fixture.txt`.
- `-l, --league=<league>` The league of the fixture, it can be `Arg` for Argentinian League or `Pre` for Premier League.
- `-p, --password=<password>` An optional password used to encrypt the prode.
- `-s, --signed` To sign the prode.
- `-m, --model=<provider>` LLM provider, it can be `OPENAI` or `GOOGLE` (with `GOOGLE` being the default)

An example would be the following
```bash
java -jar target/DesignPatterns-1.0-SNAPSHOT-jar-with-dependencies.jar -l=Arg -s --file="hello.txt" -p="hello" c
```

### Get the score of a fixture
The `score` or `s` command is used to compute the score of a prode, adding one point
for each correct prediction. The following options are available:
- `-f, --file=<fileName>` The file used to load the prode.
- `-l, --league=<league>` The league of the fixture used to create the prode.
- `-p, --password=<password>` The password used to encrypt the prode when created.
- `-s, --signed` To verify the prode was produced by this software. Must be used when the same option was used in the creation. 
- `-m, --model=<provider>` LLM provider, it can be `OPENAI` or `GOOGLE` (with `GOOGLE` being the default)

An example of use would be the following:
```bash
 java -jar target/DesignPatterns-1.0-SNAPSHOT-jar-with-dependencies.jar -l=Arg -s --file="hello.txt" -p="hello" s
```

## About
This project was made as an assignment for the subject _Application or use cases of various design patterns_.

**The team members are:**
- [Lautaro Hernando](https://github.com/laucha12)
- [José Rodolfo Mentasti](https://github.com/JoseMenta)

## Assignment 3 
For the third assignment, the following design patterns were implemented:
- Circuit breaker
- LLMProxy
- LLM Adapter


### Circuit breaker
This resilience pattern was chosen to prevent the adapters that get the information for each league.
To implement it, the `InvocationHandler` interface from `java.lang.reflect` was implemented in the 
`CircuitBreakerProxy` class, using the `CircuitBreaker` class for the logic of opening and closing 
the circuit. 

Dynamic proxies were used because they enable transparent use of the wrapped classes without adding 
interfaces, and they can be used for arbitrary classes without knowing their prior implementation or
declared methods.

To test this functionality and avoid failing in the first case (in which the circuit is opend and 
a null value is returned), a while loop was added in the `init` method of the `FixtureCli` class.

### LLM Adapter
For the creation of the fixture, a brief description of the team was added to help new users with their 
decisions. Because there are different providers of LLMs, the adapter pattern was used to define a common
interface that can be used by the application and hide the implementation details of each provider. This interface
is defined in `TextModelAdapter` in the `ai.models` package, with two concrete implementations for _OpenAi_ and _Google Gemini_ 
in the same package.


### LLMProxy
To avoid calling the LLM provider for teams that have already been described, a proxy was implemented to cache the results.
The `TeamDescriptionService` interface is defined with the methods to get the description of a team, the `TeamDescriptionServiceImpl`
uses a provided adapter to get a description with a custom prompt, and the `TeamDescriptionCacheProxy` wraps the implementation
to cache the results.

The improvements offered by this approach are notorious, and it is shown with the difference in loading time when completing 
the second date of a tournament (where most teams will be cached). 