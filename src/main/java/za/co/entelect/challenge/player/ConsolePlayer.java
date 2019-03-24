package za.co.entelect.challenge.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.player.entity.BasePlayer;
import za.co.entelect.challenge.player.entity.BotExecutionContext;

import java.util.Scanner;

public class ConsolePlayer extends BasePlayer {

    private static final Logger log = LogManager.getLogger(ConsolePlayer.class);

    private Scanner scanner;

    public ConsolePlayer(String name) {
        super(name);
        scanner = new Scanner(System.in);
    }

    @Override
    public void startGame(GameMap gameMap) {
        newRoundStarted(gameMap);
    }

    @Override
    public String getCommand(BotExecutionContext botExecutionContext) {

        String output = botExecutionContext.consoleState;
        log.info(output);

        String inputPrompt = consoleRenderer.commandPrompt(getGamePlayer());
        log.info(inputPrompt);

        return scanner.nextLine();
    }

    @Override
    public void gameEnded(GameMap gameMap) {

    }
}
