package za.co.entelect.challenge.player;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.player.entity.BasePlayer;
import za.co.entelect.challenge.player.entity.BotExecutionContext;

import java.util.Scanner;

public class ConsolePlayer extends BasePlayer {

    private static final Logger log = LogManager.getLogger(ConsolePlayer.class);

    private Scanner scanner;
    private StopWatch stopWatch;

    public ConsolePlayer(String name) {
        super(name);
        this.scanner = new Scanner(System.in);
        this.stopWatch = new StopWatch();
    }

    @Override
    public void startGame(GameMap gameMap) {
        newRoundStarted(gameMap);
    }

    @Override
    public void setExecutionResult(BotExecutionContext botExecutionContext) {

        String output = botExecutionContext.consoleState;
        log.info(output);

        String inputPrompt = consoleRenderer.commandPrompt(getGamePlayer());
        log.info(inputPrompt);

        stopWatch.reset();
        stopWatch.start();
        botExecutionContext.command =  scanner.nextLine();

        stopWatch.stop();
        botExecutionContext.executionTime = stopWatch.getTime();
    }

    @Override
    public void gameEnded(GameMap gameMap) {

    }
}
