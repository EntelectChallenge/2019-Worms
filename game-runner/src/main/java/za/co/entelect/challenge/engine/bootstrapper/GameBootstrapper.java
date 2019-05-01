package za.co.entelect.challenge.engine.bootstrapper;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import za.co.entelect.challenge.config.GameRunnerConfig;
import za.co.entelect.challenge.config.TournamentConfig;
import za.co.entelect.challenge.engine.loader.GameEngineClassLoader;
import za.co.entelect.challenge.engine.runner.GameEngineRunner;
import za.co.entelect.challenge.enums.EnvironmentVariable;
import za.co.entelect.challenge.game.contracts.bootstrapper.GameEngineBootstrapper;
import za.co.entelect.challenge.game.contracts.game.GameResult;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.player.bootstrapper.PlayerBootstrapper;
import za.co.entelect.challenge.renderer.RendererResolver;
import za.co.entelect.challenge.storage.AzureBlobStorageService;
import za.co.entelect.challenge.utils.ZipUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.UUID;

public class GameBootstrapper {

    private static final Logger LOGGER = LogManager.getLogger(GameBootstrapper.class);

    private AzureBlobStorageService blobService;

    public static void main(String[] args) throws Exception {
        setupSystemClassloader();
        new GameBootstrapper().run();
    }

    private void run() {

        GameRunnerConfig gameRunnerConfig = null;
        try {
            gameRunnerConfig = GameRunnerConfig.load("./game-runner-config.json");

            initLogging(gameRunnerConfig);
            if (gameRunnerConfig.isTournamentMode) {
                TournamentConfig tournamentConfig = gameRunnerConfig.tournamentConfig;
                blobService = new AzureBlobStorageService(tournamentConfig.connectionString);

                downloadGameEngine(gameRunnerConfig);
            }

            PlayerBootstrapper playerBootstrapper = new PlayerBootstrapper();
            List<Player> players = playerBootstrapper.loadPlayers(gameRunnerConfig);

            // Class load the game engine bootstrapper. This is the entry point for the runner into the engine
            GameEngineClassLoader gameEngineClassLoader = new GameEngineClassLoader(gameRunnerConfig.gameEngineJar);
            GameEngineBootstrapper gameEngineBootstrapper = gameEngineClassLoader.loadEngineObject(GameEngineBootstrapper.class);
            gameEngineBootstrapper.setConfigPath(gameRunnerConfig.gameConfigFileLocation);
            gameEngineBootstrapper.setSeed(gameRunnerConfig.seed);

            RendererResolver rendererResolver = new RendererResolver(gameEngineBootstrapper);

            GameEngineRunner engineRunner = new GameEngineRunner.Builder()
                    .setGameRunnerConfig(gameRunnerConfig)
                    .setGameEngine(gameEngineBootstrapper.getGameEngine())
                    .setGameMapGenerator(gameEngineBootstrapper.getMapGenerator())
                    .setRoundProcessor(gameEngineBootstrapper.getRoundProcessor())
                    .setReferee(gameEngineBootstrapper.getReferee())
                    .setRendererResolver(rendererResolver)
                    .setPlayers(players)
                    .build();

            GameResult gameResult = engineRunner.runMatch();

            if (gameRunnerConfig.isTournamentMode) {
                File zippedLogs = ZipUtils.createZip(gameRunnerConfig.matchId, gameRunnerConfig.roundStateOutputLocation);
                saveMatchLogs(gameRunnerConfig.tournamentConfig, zippedLogs, ".");
                notifyMatchComplete(gameRunnerConfig.tournamentConfig, gameResult);
            }

        } catch (Exception e) {
            LOGGER.error(e);
            e.printStackTrace();
            notifyMatchFailure(gameRunnerConfig, e);
        }
    }

    private void downloadGameEngine(GameRunnerConfig gameRunnerConfig) throws Exception {
        LOGGER.info("Downloading game engine");
        File gameEngineZip = blobService.getFile(
                System.getenv(EnvironmentVariable.GAME_ENGINE.name()),
                String.format("tournament-tmp/engine-%s.zip", UUID.randomUUID().toString()),
                gameRunnerConfig.tournamentConfig.gameEngineContainer);

        File gameEngineDir = ZipUtils.extractZip(gameEngineZip).getParentFile();
        gameRunnerConfig.gameEngineJar = gameEngineDir.listFiles((dir, name) -> name.endsWith(".jar"))[0].getPath();
        gameRunnerConfig.gameConfigFileLocation = gameEngineDir.listFiles(
                (dir, name) -> name.endsWith(".json") || name.endsWith(".properties")
        )[0].getPath();
    }

    private void initLogging(GameRunnerConfig gameRunnerConfig) {
        if (gameRunnerConfig.isVerbose) {
            Configurator.setRootLevel(Level.DEBUG);
        } else {
            Configurator.setRootLevel(Level.ERROR);
        }
    }

    private void saveMatchLogs(TournamentConfig tournamentConfig, File matchLogs, String destinationPath) throws Exception {
        LOGGER.info("Saving match logs to storage");
        blobService.putFile(matchLogs, destinationPath, tournamentConfig.matchLogsContainer);
    }

    private void notifyMatchComplete(TournamentConfig tournamentConfig, GameResult gameResult) throws Exception {
        LOGGER.info("Notifying of match completion");

        //gameResult.TournamentId = tournamentConfig.tournamentId
        //gameResult.PlayerAEntryId = System.getEnv(EnvironmentVariable.PLAYER_A_ENTRY_ID.name());

        //TODO Post to function
    }

    private void notifyMatchFailure(GameRunnerConfig gameRunnerConfig, Exception e) {
        if (gameRunnerConfig != null && gameRunnerConfig.isTournamentMode) {
            LOGGER.info("Notifying of match failure");
            //TODO Post to function
        }
    }

    private static void setupSystemClassloader() throws Exception {
        Field scl = ClassLoader.class.getDeclaredField("scl");
        scl.setAccessible(true);
        scl.set(null, new URLClassLoader(new URL[0]));
        Thread.currentThread().setContextClassLoader(new URLClassLoader(new URL[0], ClassLoader.getSystemClassLoader()));
    }
}
