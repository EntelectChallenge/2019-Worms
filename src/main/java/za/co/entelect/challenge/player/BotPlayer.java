package za.co.entelect.challenge.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.botrunners.BotRunner;
import za.co.entelect.challenge.game.contracts.exceptions.TimeoutException;
import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.player.entity.BotExecutionState;
import za.co.entelect.challenge.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BotPlayer extends BasePlayer {

    private static final String BOT_COMMAND = "command.txt";
    private static final String BOT_STATE = "state.json";
    private static final String TEXT_MAP = "textMap.txt";

    private BotRunner botRunner;

    private static final Logger log = LogManager.getLogger(BotPlayer.class);

    public BotPlayer(String name, BotRunner botRunner) {
        super(name);

        this.botRunner = botRunner;
    }

    @Override
    public void startGame(GameMap gameMap) {
        newRoundStarted(gameMap);
    }

    @Override
    public void newRoundStarted(GameMap gameMap) {

    }

    @Override
    public String getCommand(BotExecutionState botExecutionState) throws Exception {

        File existingCommandFile = new File(String.format("%s/%s", botRunner.getBotDirectory(), BOT_COMMAND));

        if (existingCommandFile.exists()) {
            existingCommandFile.delete();
        }

        FileUtils.writeToFile(String.format("%s/%s", botRunner.getBotDirectory(), BOT_STATE), botExecutionState.jsonState);
        FileUtils.writeToFile(String.format("%s/%s", botRunner.getBotDirectory(), TEXT_MAP), botExecutionState.textState);

        String botCommand = "";
        File botCommandFile = new File(String.format("%s/%s", botRunner.getBotDirectory(), BOT_COMMAND));

        try {
            botRunner.run();

            // Try to read the bot output.
            // Dispose the reader whether or not the read is successful
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(botCommandFile))) {
                botCommand = bufferedReader.readLine();
            }
        }

        catch (IOException e) {
            log.info("Bot execution failed: " + e.getLocalizedMessage());
        }

        catch (TimeoutException e) { // IOException caught first, nothing happens after
            log.info("Bot execution failed: " + e.getLocalizedMessage());
            incrementTimeoutCounts();
        }

        return botCommand;
    }

    @Override
    public void gameEnded(GameMap gameMap) {

    }
}
