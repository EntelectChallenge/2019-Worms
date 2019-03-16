package za.co.entelect.challenge.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.game.contracts.renderer.GameMapRenderer;
import za.co.entelect.challenge.game.contracts.renderer.RendererType;
import za.co.entelect.challenge.player.entity.BotExecutionContext;
import za.co.entelect.challenge.renderer.RendererResolver;

public abstract class BasePlayer extends Player {

    private static final String NO_COMMAND = "No Command";
    private static final Logger log = LogManager.getLogger(BotPlayer.class);

    private String playerId;

    protected GameMapRenderer jsonRenderer;
    protected GameMapRenderer textRenderer;
    protected GameMapRenderer consoleRenderer;

    public BasePlayer(String name) {
        super(name);
    }

    public void instantiateRenderers(RendererResolver rendererResolver) {
        jsonRenderer = rendererResolver.resolve(RendererType.JSON);
        textRenderer = rendererResolver.resolve(RendererType.TEXT);
        consoleRenderer = rendererResolver.resolve(RendererType.CONSOLE);
    }

    @Override
    public void startGame(GameMap gameMap) {
        newRoundStarted(gameMap);
    }

    @Override
    public void newRoundStarted(GameMap gameMap) {
//        @TODO Change the lifecycle in the interfaces
    }

    public BotExecutionContext executeBot(GameMap gameMap) {

        BotExecutionContext botExecutionContext = new BotExecutionContext();

        botExecutionContext.name = getName();
        botExecutionContext.jsonState = jsonRenderer.render(gameMap, getGamePlayer());
        botExecutionContext.textState = textRenderer.render(gameMap, getGamePlayer());
        botExecutionContext.consoleState = consoleRenderer.render(gameMap, getGamePlayer());
        botExecutionContext.round = gameMap.getCurrentRound();

        String command;
        try {
            // Get a command from the bot
            // The manner in which we get the command will depend
            // on the subclasses i.e Console, File, etc.
            command = getCommand(botExecutionContext);
            if (command == null || command.isEmpty()) {
                throw new Exception();
            }
        } catch (Exception e) {
            log.info("No command provided. Falling back to no command.");
            command = NO_COMMAND;
        }

        botExecutionContext.command = command;

        return botExecutionContext;
    }

    public abstract String getCommand(BotExecutionContext botExecutionContext) throws Exception;

    @Override
    public void gameEnded(GameMap gameMap) {

    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
