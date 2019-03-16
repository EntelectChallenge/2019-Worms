package za.co.entelect.challenge.engine.bootstrapper;

import com.google.gson.Gson;
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
import za.co.entelect.challenge.player.PlayerBootstrapper;
import za.co.entelect.challenge.renderer.RendererResolver;
import za.co.entelect.challenge.storage.AzureBlobStorageService;
import za.co.entelect.challenge.storage.AzureQueueStorageService;

import java.io.File;
import java.util.List;

public class GameBootstrapper {

    private static final Logger LOGGER = LogManager.getLogger(GameBootstrapper.class);

    public static void main(String[] args) {
        new GameBootstrapper().run();
    }

    private void run() {

        try {
            GameRunnerConfig gameRunnerConfig = GameRunnerConfig.load("./game-runner-config.json");

            initLogging(gameRunnerConfig);

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

        AzureBlobStorageService storageService = new AzureBlobStorageService(tournamentConfig.connectionString, tournamentConfig.matchLogsContainer);
        storageService.putFile(matchLogs, destinationPath);
    }

    private void notifyMatchComplete(TournamentConfig tournamentConfig, GameResult gameResult) throws Exception {
        LOGGER.info("Notifying of match completion");

        Gson gson = new Gson();
        String jsonResult = gson.toJson(gameResult);

        AzureQueueStorageService azureQueueStorageService = new AzureQueueStorageService(tournamentConfig.connectionString, tournamentConfig.matchResultQueue);
        azureQueueStorageService.enqueueMessage(jsonResult);
    }
}
