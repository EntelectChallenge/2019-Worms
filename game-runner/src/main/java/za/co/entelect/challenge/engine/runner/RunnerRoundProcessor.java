package za.co.entelect.challenge.engine.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.engine.exceptions.InvalidCommandException;
import za.co.entelect.challenge.engine.exceptions.InvalidOperationException;
import za.co.entelect.challenge.game.contracts.command.RawCommand;
import za.co.entelect.challenge.game.contracts.game.GamePlayer;
import za.co.entelect.challenge.game.contracts.game.GameRoundProcessor;
import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.game.contracts.player.Player;

import java.util.Hashtable;
import java.util.List;

public class RunnerRoundProcessor {
    private static final Logger log = LogManager.getLogger(RunnerRoundProcessor.class);

    private GameMap gameMap;
    private GameRoundProcessor gameRoundProcessor;

    private boolean roundProcessed;
    private Hashtable<GamePlayer, RawCommand> commandsToProcess;

    RunnerRoundProcessor(GameMap gameMap, GameRoundProcessor gameRoundProcessor) {
        this.gameMap = gameMap;
        this.gameRoundProcessor = gameRoundProcessor;

        commandsToProcess = new Hashtable<>();
    }

    boolean processRound() throws Exception {
        if (roundProcessed) {
            throw new InvalidOperationException("This round has already been processed!");
        }
        boolean processed = gameRoundProcessor.processRound(gameMap, commandsToProcess);

        List<String> errorList = gameRoundProcessor.getErrorList(gameMap);
        for (String error : errorList) {
            log.error(error);
        }

        roundProcessed = true;

        return processed;
    }

    synchronized void addPlayerCommand(Player player, RawCommand command) {
        try {
            if (commandsToProcess.containsKey(player.getGamePlayer()))
                throw new InvalidCommandException("Player already has a command registered for this round, wait for the next round before sending a new command");

            commandsToProcess.put(player.getGamePlayer(), command);
        } catch (InvalidCommandException e) {
            log.error(e.getStackTrace());
        }
    }
}
