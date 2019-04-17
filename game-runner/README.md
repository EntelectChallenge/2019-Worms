# Game Runner

## Contents
- [Game Runner](#game-runner)
  - [Overview](#overview)
  - [Getting started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
      - [Windows](#windows)
      - [Linux](#linux)
    - [Usage](#usage)
      - [Windows](#windows-1)
      - [Linux](#linux-1)
  - [Additional languages](#additional-languages)
    - [Adding a language](#adding-a-language)

## Overview
The game runner is responsible for facilitating a match between two bots. It can be seen as a proxy that relays information between the [bots](../starter-bots/README.md) and the [game engine](../game-engine/README.md). The game engine produces state information which the game runner passes onto the bots. Once the bots have processed the state and produced a command, that command is then consumed by the game runner and passed back to the game engine, this process continues until the match ends.

The bots used in a match are started up by the game runner. These bots will run continuously throughout the match. Communication with the bots is facilitated using Standard Input (stdin), Output (stdout) and Error (stderr). 

The game runner is used for both local matches as well as tournament matches. **Note**: the latest release of the game runner will be used to run the matches between contestants during the tournament.

## Getting started
### Prerequisites 
- JDK 1.8
  - [Windows](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  - [Linux](https://openjdk.java.net/install/)
- [Apache Maven](https://maven.apache.org/download.cgi)

### Installation
Both a Windows batch file, as well as a makefile, are provided to assist with building the game runner.

#### Windows 
Simply double click on `build.bat` to start the build process. 
Alternatively, open the Command Prompt in the game runner directory and execute the following command:
```
build.bat
```

#### Linux 
Simply open a terminal in the game runner directory and execute the following command:
```
make
```

### Usage
The `game-runner-config.json` file consists of all the necessary information required to run a match. The config file is already present when the starter pack is downloaded and will have default values for each of its fields. When running a match locally the correct values have to be set in the config. 

The `game-runner-config.json` has the following fields:
- `round-state-output-location` - This is the path to where you want the match folder in which each round's folder with its respective logs will be saved.
  
- `game-config-file-location` - This is the path to the config file used by the game engine. It contains all necessary game information.
  
- `game-engine-jar` - This is the path to the game engine Jar file. The game runner uses this field to load a game engine for a match. The ability to specify game engines allows for a contestant to run local matches with different versions of the game engine
  
- `verbose-mode` - This is a `true` or `false` value to either print logs to the console or not respectively. By default this is set to `true`.
  
- `max-runtime-ms` - This is the amount of milliseconds that the game runner will allow a bot to run before making its command each round.
  
- `player-a` & 
- `player-b` - This is the path to the folder containing the 'bot.json' file. If you would like to replace one of the bot players with a console player, just use the word "console" as the path.
These are the paths to the respective bots and need to be updated to point to the new bots the contestant wants to use in a match.
  
- `player-a-id` & 
  
- `player-b-id` - These are used to uniquely identify each player.
   
- `max-request-retries` - This is used for network based requests. It dictates how many times a request will be retried before finally failing.
  
- `request-timeout-ms` - This refers to the amount of time that must elapse before retrying a request.
  
- `is-tournament-mode` - This field states whether or not the current match is a tournament match. When set to `true` it means the match is a tournament match, similairly, when set to `false` it's a local match. By default this is set to `false`.
  
- `tournament` - This is a tournament configutation object that will hold all the necessary config for the tournament.
  
Not all of these fields need to be changed, as stated above, the starter pack will come with default values for these fields. The starter pack will always contain the latest version of the game engine, as well as the game engine`s configuration. 

All that needs to change for a local match are the following:
- `player-a`
- `player-b`
- `verbose-mode`

Once the correct fields are set a match can be run. Once again, a Windows batch file and a makefile are provided to assist.
#### Windows 
Simply double click on `run.bat` to start up a match. 
Alternatively, open the Command Prompt in the game runner directory and execute the following command:
```
run.bat
```

#### Linux 
Simply open a terminal in the game runner directory and execute the following command:
```
make run
```

## Additional languages
The game runner currently supports four languages, however, it can be easily extended to support more. The following four languages are currently supported:
- .Net Core (C#)
- Python
- Javascript
- Java

The starter bots for each of these languages can be found [here](../starter-bots/).

When adding a new language the following steps need to be followed:
1. Create a new bot runner for your language (Discussed in the section below)
2. Create a starter bot with the instructions found [here](../starter-bots/README.md)

Once these steps have completed, a Pull Request with the new language and starter bot can be made to this repository where it will be reviewed by the Entelect Challenge Team.

### Adding a language
When adding a new language to the game runner a new `BotRunner` is required. Each implementation of this class is responsible for starting up a bot of a given language. For example, there exists a `JavaBotRunner` and its purpose is to start-up a Java bot, similarly there exists a `JavaScriptBotRunner` that starts-up a JavaScript bot. `BotRunner``s for the supported languages listed above are provided by default.

To add a new language, a new `BotRunner` needs to be created, therefore:
```java
public class JavaBotRunner extends BotRunner
```
Once a new implementation is created, the `runBot()` method needs to be implemented:
```java
@Override
protected void runBot() throws IOException {
    String line = "java -jar \"" + this.getBotFileName() + "\"";
    runSimpleCommandLineCommand(line, 0);
}
```
In the code sample above a new command is created. This command is used to start-up a bot of that specific language. The `getBotFileName()` method will return the path to the executable, in this case, it will be the path to the Jar, for JavaScript, it will be the path to the script. **Note**: that this command should be able to run on Command Prompt (Windows) and Terminal (Linux). Once the command is created simply call the `runSimpleCommandLineCommand()` method with the command as the first parameter and the expected exit code as the second parameter (usually 0).

Once the `BotRunner` is created the language needs to be added to the `BotLanguage` file:
```java
public enum BotLanguage {
    @SerializedName("java")
    JAVA
}
```
The `@SerializedName()` will take in the text version of the language type. This text version of the language is used in the `bot.json` file, for example, the Java starter bot has the following `bot.json`:
```
{
    "author": "John Doe",
    "email": "john.doe@example.com",
    "nickName": "James",
    "botLocation": "/target",
    "botFileName": "java-sample-bot-jar-with-dependencies.jar",
    "botLanguage": "java"
}
```
Note that the `botLanguage` is the same as the text in `@SerializedName()`. This allows the game runner to determine the bot`s language. 

Once the language is added a link needs to be established between the new language and its `BotRunner`. To do this the `BotRunnerFactory` needs to be updated. Therefore, the following entry would be added:
```java
case JAVA:
    return new JavaBotRunner(botMetaData, timeoutInMilliseconds);
```
Now the game runner will support your new language. Before submitting your new bot please ensure that step two from the above steps has been followed.