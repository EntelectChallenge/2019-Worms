package za.co.entelect.challenge.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.core.renderers.TowerDefenseConsoleMapRenderer;
import za.co.entelect.challenge.engine.runner.GameEngineRunner;
import za.co.entelect.challenge.game.contracts.command.RawCommand;
import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.game.contracts.renderer.GameMapRenderer;

import java.util.Scanner;

public class ConsolePlayer extends Player {

    private static final Logger log = LogManager.getLogger(ConsolePlayer.class);

    private GameMapRenderer gameMapRenderer;
    private Scanner scanner;

    public ConsolePlayer(String name) {
        super(name);

        scanner = new Scanner(System.in);
        gameMapRenderer = new TowerDefenseConsoleMapRenderer();
    }

    @Override
    public void startGame(GameMap gameMap) {
        newRoundStarted(gameMap);
    }

    @Override
    public void newRoundStarted(GameMap gameMap) {

        String output = gameMapRenderer.render(gameMap, getGamePlayer());
        log.info(output);

        String inputPrompt = gameMapRenderer.commandPrompt(getGamePlayer());
        log.info(inputPrompt);

        String consoleInput = scanner.nextLine();

        RawCommand rawCommand = new RawCommand(consoleInput);
        publishCommand(rawCommand);
    }

    @Override
    public void gameEnded(GameMap gameMap) {

    }
}
