package za.co.entelect.challenge.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.core.renderers.TowerDefenseConsoleMapRenderer;
import za.co.entelect.challenge.core.renderers.TowerDefenseJsonGameMapRenderer;
import za.co.entelect.challenge.core.renderers.TowerDefenseTextMapRenderer;
import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.game.contracts.renderer.GameMapRenderer;
import za.co.entelect.challenge.player.entity.BotExecutionState;

public abstract class BasePlayer extends Player {

    private static final String NO_COMMAND = "No Command";
    private static final Logger log = LogManager.getLogger(BotPlayer.class);

    private GameMapRenderer jsonRenderer;
    private GameMapRenderer textRenderer;
    private GameMapRenderer consoleRenderer;

    public BasePlayer(String name) {
        super(name);

        jsonRenderer = new TowerDefenseJsonGameMapRenderer();
        textRenderer = new TowerDefenseTextMapRenderer();
        consoleRenderer = new TowerDefenseConsoleMapRenderer();
    }

    @Override
    public void startGame(GameMap gameMap) {
        newRoundStarted(gameMap);
    }

    @Override
    public void newRoundStarted(GameMap gameMap) {
//        @TODO Change the lifecycle in the interfaces
    }

    public BotExecutionState executeBot(GameMap gameMap) {

        BotExecutionState botExecutionState = new BotExecutionState();

        botExecutionState.name = getName();
        botExecutionState.jsonState = jsonRenderer.render(gameMap, getGamePlayer());
        botExecutionState.textState = textRenderer.render(gameMap, getGamePlayer());
        botExecutionState.consoleState = consoleRenderer.render(gameMap, getGamePlayer());
        botExecutionState.round = gameMap.getCurrentRound();

        String command;
        try {
            // Get a command from the bot
            // The manner in which we get the command will depend
            // on the subclasses i.e Console, File, etc.
            command = getCommand(botExecutionState);
            if (command == null || command.isEmpty()) {
                throw new Exception();
            }
        }

        catch (Exception e) {
            log.info("No command provided. Falling back to no command.");
            command = NO_COMMAND;
        }

        botExecutionState.command = command;

        return botExecutionState;
    }

    public abstract String getCommand(BotExecutionState botExecutionState) throws Exception;

    @Override
    public void gameEnded(GameMap gameMap) {

    }
}
