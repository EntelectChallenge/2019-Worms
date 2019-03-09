package za.co.entelect.challenge.engine.bootstrapper;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import za.co.entelect.challenge.config.GameRunnerConfig;
import za.co.entelect.challenge.engine.loader.GameEngineClassLoader;
import za.co.entelect.challenge.engine.runner.GameEngineRunner;
import za.co.entelect.challenge.game.contracts.bootstrapper.GameEngineBootstrapper;
import za.co.entelect.challenge.game.contracts.game.GameResult;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.player.PlayerBootstrapper;
import za.co.entelect.challenge.renderer.RendererResolver;

import java.util.List;

public class GameBootstrapper {

    private static final Logger LOGGER = LogManager.getLogger(GameBootstrapper.class);

    public static void main(String[] args) {
        new GameBootstrapper().run();
    }

    private GameResult run() {

        try {
            GameRunnerConfig gameRunnerConfig = GameRunnerConfig.load("./game-runner-config.json", null);

            initLogging(gameRunnerConfig);

            PlayerBootstrapper playerBootstrapper = new PlayerBootstrapper();
            List<Player> players = playerBootstrapper.loadPlayers(gameRunnerConfig);

            GameEngineClassLoader gameEngineClassLoader = new GameEngineClassLoader(gameRunnerConfig.gameEngineJar);

            // Class load the game engine bootstrapper. This is the entry point for the runner into the engine
            GameEngineBootstrapper gameEngineBootstrapper = gameEngineClassLoader.loadEngineObject(GameEngineBootstrapper.class);
            gameEngineBootstrapper.setConfigPath(gameRunnerConfig.gameConfigFileLocation);
            gameEngineBootstrapper.setSeed(System.nanoTime());

            RendererResolver rendererResolver = new RendererResolver(gameEngineBootstrapper);

            GameEngineRunner engineRunner = new GameEngineRunner.Builder()
                    .setGameRunnerConfig(gameRunnerConfig)
                    .setGameEngine(gameEngineBootstrapper.getGameEngine())
                    .setGameMapGenerator(gameEngineBootstrapper.getMapGenerator())
                    .setRoundProcessor(gameEngineBootstrapper.getRoundProcessor())
                    .setRendererResolver(rendererResolver)
                    .setPlayers(players)
                    .build();

            engineRunner.runMatch();

        } catch (Exception e) {
            LOGGER.error(e);
            e.printStackTrace();
        }

        return null;
    }

    private void initLogging(GameRunnerConfig gameRunnerConfig) {

        if (gameRunnerConfig.isVerbose) {
            Configurator.setRootLevel(Level.DEBUG);
        } else {
            Configurator.setRootLevel(Level.ERROR);
        }
    }
}
