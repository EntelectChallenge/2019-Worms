package za.co.entelect.challenge.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.botrunners.BotRunner;
import za.co.entelect.challenge.core.renderers.TowerDefenseConsoleMapRenderer;
import za.co.entelect.challenge.core.renderers.TowerDefenseJsonGameMapRenderer;
import za.co.entelect.challenge.core.renderers.TowerDefenseTextMapRenderer;
import za.co.entelect.challenge.game.contracts.command.RawCommand;
import za.co.entelect.challenge.game.contracts.exceptions.TimeoutException;
import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.game.contracts.renderer.GameMapRenderer;
import za.co.entelect.challenge.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class BotPlayer extends Player {

    private static final String BOT_COMMAND = "command.txt";
    private static final String BOT_STATE = "state.json";
    private static final String TEXT_MAP = "textMap.txt";
    private GameMapRenderer jsonRenderer;
    private GameMapRenderer textRenderer;
    private GameMapRenderer consoleRenderer;
    private BotRunner botRunner;
    private String saveStateLocation;

    private static final Logger log = LogManager.getLogger(BotPlayer.class);

    public BotPlayer(String name, BotRunner botRunner, String saveStateLocation) {
        super(name);

        jsonRenderer = new TowerDefenseJsonGameMapRenderer();
        textRenderer = new TowerDefenseTextMapRenderer();
        consoleRenderer = new TowerDefenseConsoleMapRenderer();

        this.botRunner = botRunner;
        this.saveStateLocation = saveStateLocation;
    }

    @Override
    public void startGame(GameMap gameMap) {
        newRoundStarted(gameMap);
    }

    @Override
    public void newRoundStarted(GameMap gameMap) {
        String playerSpecificJsonState = jsonRenderer.render(gameMap, getGamePlayer());
        String playerSpecificTextState = textRenderer.render(gameMap, getGamePlayer());
        String playerSpecificConsoleState = consoleRenderer.render(gameMap, getGamePlayer());
        String consoleOutput = "";
        try {
            consoleOutput = runBot(playerSpecificJsonState, playerSpecificTextState);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //receive response from bot
        String botInput = "";
        File botCommandFile = new File(String.format("%s/%s", botRunner.getBotDirectory(), BOT_COMMAND));
        try (Scanner scanner = new Scanner(botCommandFile)) {
            if (scanner.hasNext()) {
                botInput = scanner.nextLine();
            } else {
                botInput = "No Command";
            }
        } catch (FileNotFoundException e) {
            log.info(String.format("File %s not found", botRunner.getBotDirectory() + "/" + BOT_COMMAND));
        }
        try {
            BotPlayer.writeRoundStateData(playerSpecificJsonState,
                    playerSpecificTextState,
                    playerSpecificConsoleState,
                    botInput,
                    gameMap.getCurrentRound(),
                    consoleOutput,
                    getName(),
                    saveStateLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }

        RawCommand rawCommand = new RawCommand(botInput);
        publishCommand(rawCommand);
    }

    private String runBot(String state, String textState) throws IOException {
        File existingCommandFile = new File(String.format("%s/%s", botRunner.getBotDirectory(), BOT_COMMAND));

        if (existingCommandFile.exists()) {
            existingCommandFile.delete();
        }

        FileUtils.writeToFile(String.format("%s/%s", botRunner.getBotDirectory(), BOT_STATE), state);
        FileUtils.writeToFile(String.format("%s/%s", botRunner.getBotDirectory(), TEXT_MAP), textState);

        String botConsoleOutput = "";

        try {
            botConsoleOutput = botRunner.run();
        } catch (IOException e) {
            log.info("Bot execution failed: " + e.getLocalizedMessage());
        } catch (TimeoutException e) { // IOException caught first, nothing happens after
            log.info("Bot execution failed: " + e.getLocalizedMessage());
            incrementTimeoutCounts();
        }
        return botConsoleOutput;
    }

    static void writeRoundStateData(String playerSpecificJsonState,
                                    String playerSpecificTextState,
                                    String playerSpecificConsoleState,
                                    String command,
                                    int round,
                                    String botConsoleOutput,
                                    String playerName,
                                    String saveStateLocation) throws IOException {
        String mainDirectory = String.format("%s/%s", saveStateLocation, FileUtils.getRoundDirectory(round));
        File fMain = new File(mainDirectory);
        if (!fMain.exists()) {
            fMain.mkdirs();
        }

        File f = new File(String.format("%s/%s", mainDirectory, playerName));
        if (!f.exists()) {
            f.mkdirs();
        }

        File fConsole = new File(String.format("%s/%s/%s", mainDirectory, playerName, "Console"));
        if (!fConsole.exists()) {
            fConsole.mkdirs();
        }

        FileUtils.writeToFile(String.format("%s/%s/%s", mainDirectory, playerName, "JsonMap.json"), playerSpecificJsonState);
        FileUtils.writeToFile(String.format("%s/%s/%s", mainDirectory, playerName, "TextMap.txt"), playerSpecificTextState);
        FileUtils.writeToFile(String.format("%s/%s/%s", mainDirectory, playerName, "PlayerCommand.txt"), command);
        FileUtils.writeToFile(String.format("%s/%s/%s/%s", mainDirectory, playerName, "Console", "Console.txt"), playerSpecificConsoleState);
        FileUtils.writeToFile(String.format("%s/%s/%s/%s", mainDirectory, playerName, "Console", "BotOutput.txt"), botConsoleOutput);
    }

    @Override
    public void gameEnded(GameMap gameMap) {
        String playerSpecificJsonState = jsonRenderer.render(gameMap, getGamePlayer());
        String playerSpecificTextState = textRenderer.render(gameMap, getGamePlayer());
        String playerSpecificConsoleState = consoleRenderer.render(gameMap, getGamePlayer());

        try {
            writeRoundStateData(playerSpecificJsonState,
                    playerSpecificTextState,
                    playerSpecificConsoleState,
                    "",
                    gameMap.getCurrentRound(),
                    "",
                    getName(),
                    saveStateLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
