# ⚽️ Prode
This project implements a _Prode_, a popular argentinain game where a person tries to predict the results of the 
fixture of a football league, and gains a point for each correct prediction. A prediction is considered correct when 
the exact result of the match is predicted, not only the winner of the match. 

[!WARNING]  
This project is not intended to be used for gambling purposes. It was made because it's a popular argentinian game 
intended to be played with families and friends. 

## Table of contents 
- [Requirements](#requirements)


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

## Execute the cli tool 
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

An example of use would be the following:
```bash
 java -jar target/DesignPatterns-1.0-SNAPSHOT-jar-with-dependencies.jar -l=Arg -s --file="hello.txt" -p="hello" s
```

## About
This project was made as an assignment for the subject _Application or use cases of various design patterns_.

**The team members are:**
- [Lautaro Hernando](https://github.com/laucha12)
- [José Rodolfo Mentasti](https://github.com/JoseMenta)