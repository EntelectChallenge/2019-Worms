package za.co.entelect.challenge.engine.bootstrapper;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import za.co.entelect.challenge.config.GameRunnerConfig;
import za.co.entelect.challenge.config.TournamentConfig;
import za.co.entelect.challenge.engine.loader.GameEngineClassLoader;
import za.co.entelect.challenge.engine.runner.GameEngineRunner;
import za.co.entelect.challenge.game.contracts.bootstrapper.GameEngineBootstrapper;
import za.co.entelect.challenge.game.contracts.game.GameResult;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.network.Dto.RunnerFailedDto;
import za.co.entelect.challenge.player.PlayerBootstrapper;
import za.co.entelect.challenge.renderer.RendererResolver;
import za.co.entelect.challenge.storage.AzureBlobStorageService;
import za.co.entelect.challenge.storage.AzureQueueStorageService;

import java.io.File;
import java.util.List;

public class GameBootstrapper {

    private static final Logger LOGGER = LogManager.getLogger(GameBootstrapper.class);

    private AzureBlobStorageService blobService;
    private AzureQueueStorageService queueService;

    public static void main(String[] args) {
        new GameBootstrapper().run();
    }

    private void run() {

        GameRunnerConfig gameRunnerConfig = null;
        try {
            gameRunnerConfig = GameRunnerConfig.load("./game-runner-config.json");

            initLogging(gameRunnerConfig);
            if (gameRunnerConfig.isTournamentMode) {
                TournamentConfig tournamentConfig = gameRunnerConfig.tournamentConfig;
                blobService = new AzureBlobStorageService(tournamentConfig.connectionString, tournamentConfig.matchLogsContainer);
                queueService = new AzureQueueStorageService(tournamentConfig.connectionString);
            }

            PlayerBootstrapper playerBootstrapper = new PlayerBootstrapper();
            List<Player> players = playerBootstrapper.loadPlayers(gameRunnerConfig);

            GameEngineClassLoader gameEngineClassLoader = new GameEngineClassLoader(gameRunnerConfig.gameEngineJar);
            // Class load the game engine bootstrapper. This is the entry point for the runner into the engine
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
                File zippedLogs = zipMatchLogs(gameRunnerConfig.matchId, gameRunnerConfig.roundStateOutputLocation);
                saveMatchLogs(gameRunnerConfig.tournamentConfig, zippedLogs, ".");
                notifyMatchComplete(gameRunnerConfig.tournamentConfig, gameResult);
            }

        } catch (Exception e) {
            LOGGER.error(e);
            e.printStackTrace();
            notifyMatchFailure(gameRunnerConfig, e);
        }
    }

    private void notifyMatchFailure(GameRunnerConfig gameRunnerConfig, Exception e) {
        if (gameRunnerConfig != null && gameRunnerConfig.isTournamentMode) {
            try {
                queueService.enqueueMessage(gameRunnerConfig.tournamentConfig.deadMatchQueue,
                        new RunnerFailedDto(gameRunnerConfig.matchId, e));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initLogging(GameRunnerConfig gameRunnerConfig) {

        if (gameRunnerConfig.isVerbose) {
            Configurator.setRootLevel(Level.DEBUG);
        } else {
            Configurator.setRootLevel(Level.ERROR);
        }
    }

    private File zipMatchLogs(String matchId, String localMatchLogsDir) throws Exception {

        ZipFile zipFile = new ZipFile(new File(String.format("%s.zip", matchId)));

        File path = new File(localMatchLogsDir);
        path = path.listFiles()[0];

        ZipParameters parameters = new ZipParameters();
        zipFile.addFolder(path, parameters);

        return zipFile.getFile();
    }

    private void saveMatchLogs(TournamentConfig tournamentConfig, File matchLogs, String destinationPath) throws Exception {
        LOGGER.info("Saving match logs to storage");
        blobService.putFile(matchLogs, destinationPath);
    }

    private void notifyMatchComplete(TournamentConfig tournamentConfig, GameResult gameResult) throws Exception {
        LOGGER.info("Notifying of match completion");
        queueService.enqueueMessage(tournamentConfig.matchResultQueue, gameResult);
    }
}
