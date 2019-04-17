# Entelect Challenge 2019 - Worms

The Entelect Challenge is an annual coding competition where students, professional developers, and enthusiasts develop an intelligent bot to play a game.

This year, the game is Worms. We have made it as simple as possible to get started. Just follow the steps below.

## Step 1 - Download
Download the starter pack zip file and extract it to the folder you want to work in.

## Step 2 - Run
Follow the instructions in the read me files to run your first match between two of the provided starter bots.

## Step 3 - Improve
Change some of the provided logic or code your own into one of the given starter bots and upload it to the player portal.

## WIN!!!
For more information, visit one of the following:

[Our website](https://challenge.entelect.co.za)

[Our forum](https://forum.entelect.co.za)

Or read the rules in the [game-rules.md](game-engine/game-rules.md) file.

## Project Structure

In this project you will find everything we use to build a starter pack that you can use to run a bot on your local machine.  This project contains the following:

1. **game-engine-interface** - The generic interface that the game runner uses to be able to plug in different game engines.
2. **game-engine** - The game engine is responsible for enforcing the rules of the game, by applying the bot commands to the game state if they are valid.
3. **game-runner** - The game runner is responsible for running matches between players, calling the appropriate commands as given by the bots and handing them to the game-engine to execute.
4. **reference-bot** - The reference bot contains some AI logic that will play the game based on predefined rules.  You can use this to play against your bot for testing purposes.
5. **starter-bots** - Starter bots with limited logic that can be used a starting point for your bot.

This project can be used to get a better understanding of the rules and to help debug your bot.

Improvements and enhancements will be made to the game engine code over time.  The game engine will also evolve during the competition after every battle, so be prepared. Any changes made to the game engine or rules will be updated here, so check in here now and then to see the latest changes and updates.

The game engine has been made available to the community for peer review and bug fixes, so if you find any bugs or have any concerns, please [e-mail us](challenge@entelect.co.za) or discuss it with us on the [Challenge forum](http://forum.entelect.co.za/), alternatively submit a pull request on Github and we will review it.

## Starter Pack
The starter pack will provide you with everything that you'll need to run your first bot and compete in this year's challenge. To get the starter pack, simply download the latest release found [here](github/releases).

A more in-depth explaination of the concepts below can be found [here](./game-runner/README.md).

### Step 1
Let's start off by running your very first match:
(Because we are going to run Java files, please make sure you have java installed [http://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html])

We included some 'run' files for various operating systems to run a match.

    On Windows:
        Simply run (double-click) the "run.bat" file, or open up Command Prompt in the starter pack's root directory and run the "run.bat" command.

    On Linux:
        Open the terminal in the starter pack's root directory and run the "make run" command.

You should now see some text whizz by in the console/terminal. If this is not the case, your match didn't run as it should. If the error message isn't clear, you can ask for help on our forum [forum.entelect.co.za].

### Step 2
Now let's change things up a little bit. The previous match we ran, was between the Reference bot and the Java starter bot. Let's change the match to be between the Reference bot and a starter bot of your choice. To change this we need to edit the "game-runner-config.json" file.

The most improtant fields in the 'game-runner-config.json' for running a match locally is as follows:

    "round-state-output-location" => This is the path to where you want the match folder in which each round's folder with its respective logs will be saved.

    "verbose-mode" => This is a true or false value to either print logs to the console or not respectively.

    "max-runtime-ms" => This is the amount of milliseconds that the game runner will allow a bot to run before making its command each round.

    "player-a" &
    "player-b" => This is the path to the folder containing the 'bot.json' file. If you would like to replace one of the bot players with a console player, just use the word "console" as the path.
**Note**: a full description of the 'game-runner-config.json' can be found [here](./game-runner/README.md).

The format of the 'bot.json' is as follows (also see the example in "/examples/example-bot.json"):

    "author" => This is the name of the person who wrote the bot.

    "email" => This is an email address where the author of the bot can be contacted if there are any questions.

    "nickName" => This is a nickname for the bot that will be used in visualisations.

    "botLocation" => This is a relative path to the folder containing the compiled bot file for the specific language.

    "botFileName" => This is the compiled bot file's name.

    "botLanguage" => This is the language name in which the bot is coded. A list of the currently supported languages and the names used in the runner can be found below.

To change the bots that play in the match, replace the "player-a" value in the "game-runner-config.json" file with the path to another starter bots' folder containing "bot.json".

#### Languages
- Java            => "java"
- C# (.net core)  => "c#core"
- Python3         => "python3"
- Javascript      => "javascript"
  
The prerequisites for these languages can found [here](./bot-prerequisites.md).

### Step 3
All that is left to do is to modify the existing logic or to code your own, into one of the starter bots. This will require you to do some coding in the language of your choice.

Here is a brief explanation of how a bot should work:

    The bot will be started up at the beginning of a match. The bot will continue to run through the entire match. Communication with the bot is facilitated with Standard Input and Output.

    For each round the bot should go through the following process:

        Read in the current round number from Standard in (stdin)

        Next, read in the "state.json" file that contains the game map and all the round properties.

        Apply your logic to these properties and decide on what your next move is and where you want to apply it.

        Finally, output your move to Standard out (stdout). 

The `state.json` simply keeps track of everything on the map, the worm's health, position and scores. For an example of a state file, see `/examples/example-state.json`.

The output will have the structure `C;<round_number>;<command>`, due to the fact that the bot is continously running this will allow the game runner to determine for which round certain commands are for. The starter bots already have this output format.

## Bot upload archive structure

Now that you're running matches locally you're free to create your own bot. Please note that when submitting your bot your archive should contain at least a bot.json with your source code in the **root** of the archive.

```
{bot-archive-name}.zip
|--- bot.json
|--- {source_code}
```

For example the Java sample bot has the following structure:
```
|--- bot.json
|--- src
   |--- main
      |--- {package_directories}
         |--- Bot.java
         |--- Main.java
         |--- entities
            |--- Worm.java
            |--- Player.java
            |--- Cell.java
            |--- GameMap.java
         |--- enum
            |--- CellType.java
            |--- Direction.java
```