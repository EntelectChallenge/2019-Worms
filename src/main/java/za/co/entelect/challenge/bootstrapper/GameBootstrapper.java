package za.co.entelect.challenge.bootstrapper;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import za.co.entelect.challenge.botrunners.BotRunner;
import za.co.entelect.challenge.botrunners.BotRunnerFactory;
import za.co.entelect.challenge.config.BotMetaData;
import za.co.entelect.challenge.config.GameRunnerConfig;
import za.co.entelect.challenge.engine.loader.GameEngineClassLoader;
import za.co.entelect.challenge.engine.runner.GameEngineRunner;
import za.co.entelect.challenge.game.contracts.bootstrapper.EngineBootstrapper;
import za.co.entelect.challenge.game.contracts.game.GameMapGenerator;
import za.co.entelect.challenge.game.contracts.game.GameResult;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.game.contracts.renderer.RendererFactory;
import za.co.entelect.challenge.player.BotPlayer;
import za.co.entelect.challenge.player.ConsolePlayer;
import za.co.entelect.challenge.player.TournamentPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class GameBootstrapper {

    private static final Logger log = LogManager.getLogger(GameBootstrapper.class);

    public static void main(String[] args) {
        new GameBootstrapper().run(args);
    }

    private GameResult run(String[] args) {

        try {
            GameRunnerConfig gameRunnerConfig = GameRunnerConfig.load("./game-runner-config.json", args);

            initLogging(gameRunnerConfig);
            List<Player> players = loadPlayers(gameRunnerConfig);

            GameEngineClassLoader gameEngineClassLoader = new GameEngineClassLoader(gameRunnerConfig.gameEngineJar);

            EngineBootstrapper engineBootstrapper = loadEngineBootstrapper(gameEngineClassLoader);
            RendererFactory rendererFactory = gameEngineClassLoader.loadEngineObject(RendererFactory.class);

            GameEngineRunner engineRunner = new GameEngineRunner.Builder()
                    .setGameRunnerConfig(gameRunnerConfig)
                    .setGameEngine(engineBootstrapper.getGameEngine())
                    .setGameMapGenerator(engineBootstrapper.getGameMapGenerator())
                    .setRoundProcessor(engineBootstrapper.getRoundProcessor())
                    .setRendererFactory(rendererFactory)
                    .setPlayers(players)
                    .build();

            engineRunner.runMatch();
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }

        return null;
    }

    private EngineBootstrapper loadEngineBootstrapper(GameEngineClassLoader gameEngineClassLoader) throws Exception {

        EngineBootstrapper engineBootstrapper = gameEngineClassLoader.loadEngineObject(EngineBootstrapper.class);
        engineBootstrapper.createEngineEntities();

        return engineBootstrapper;
    }

    private void initLogging(GameRunnerConfig gameRunnerConfig) {

        if (gameRunnerConfig.isVerbose) {
            Configurator.setRootLevel(Level.DEBUG);
        } else {
            Configurator.setRootLevel(Level.ERROR);
        }
    }

    private List<Player> loadPlayers(GameRunnerConfig gameRunnerConfig) throws Exception {
        List<Player> players = new ArrayList<>();

        players.add(parsePlayer(gameRunnerConfig.playerAConfig, "A", gameRunnerConfig));
        players.add(parsePlayer(gameRunnerConfig.playerBConfig, "B", gameRunnerConfig));

        return players;
    }

    private Player parsePlayer(String playerConfig, String playerNumber, GameRunnerConfig gameRunnerConfig) throws Exception {
        if (playerConfig.equals("console")) {
            return new ConsolePlayer(String.format("BotPlayer %s", playerNumber));
        } else {
            BotMetaData botConfig = BotMetaData.load(playerConfig);
            BotRunner botRunner = BotRunnerFactory.createBotRunner(botConfig, gameRunnerConfig.maximumBotRuntimeMilliSeconds);

            File botFile = new File(botConfig.getBotDirectory());
            if (!botFile.exists()) {
                throw new FileNotFoundException(String.format("Could not find %s bot file for %s(%s)", botConfig.getBotLanguage(), botConfig.getAuthor(), botConfig.getNickName()));
            }

            if (gameRunnerConfig.isTournamentMode)
                return new TournamentPlayer(String.format("%s - %s", playerNumber, botConfig.getNickName()), botRunner, botConfig.getBotLanguage());
            else
                return new BotPlayer(String.format("%s - %s", playerNumber, botConfig.getNickName()), botRunner);
        }
    }
}
