package za.co.entelect.challenge.player.bootstrapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.botrunners.BotRunner;
import za.co.entelect.challenge.botrunners.BotRunnerFactory;
import za.co.entelect.challenge.config.BotMetaData;
import za.co.entelect.challenge.config.GameRunnerConfig;
import za.co.entelect.challenge.config.TournamentConfig;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.player.entity.BasePlayer;
import za.co.entelect.challenge.player.BotPlayer;
import za.co.entelect.challenge.player.ConsolePlayer;
import za.co.entelect.challenge.player.TournamentPlayer;
import za.co.entelect.challenge.storage.AzureBlobStorageService;
import za.co.entelect.challenge.enums.EnvironmentVariable;
import za.co.entelect.challenge.utils.ZipUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerBootstrapper {

    private static final Logger LOGGER = LogManager.getLogger(PlayerBootstrapper.class);

    public List<Player> loadPlayers(GameRunnerConfig gameRunnerConfig) throws Exception {
        List<Player> players = new ArrayList<>();

        // During a tournament match we need retrieve the submitted bots from storage.
        // Once we retrieve them we can extract and read in the bot information
        if (gameRunnerConfig.isTournamentMode) {
            TournamentConfig tournamentConfig = gameRunnerConfig.tournamentConfig;

            LOGGER.info("Retrieving bot path from directory");
            String playerAEnv = System.getenv(EnvironmentVariable.PLAYER_A.name());
            String playerBEnv = System.getenv(EnvironmentVariable.PLAYER_B.name());

            LOGGER.info("Downloading bots");
            AzureBlobStorageService storageService = new AzureBlobStorageService(tournamentConfig.connectionString);
            File playerAZip = storageService.getFile(playerAEnv, String.format("./tournament-tmp/player-%s.zip", UUID.randomUUID()), tournamentConfig.botsContainer);
            File playerBZip = storageService.getFile(playerBEnv, String.format("./tournament-tmp/player-%s.zip", UUID.randomUUID()), tournamentConfig.botsContainer);

            gameRunnerConfig.playerAConfig = ZipUtils.extractZip(playerAZip).getPath();
            gameRunnerConfig.playerBConfig = ZipUtils.extractZip(playerBZip).getPath();

            players.add(parsePlayer(gameRunnerConfig.playerAConfig, "A", gameRunnerConfig, gameRunnerConfig.playerAId, playerAZip, 55555));
            players.add(parsePlayer(gameRunnerConfig.playerBConfig, "B", gameRunnerConfig, gameRunnerConfig.playerBId, playerBZip, 55556));
        } else {
            players.add(parsePlayer(gameRunnerConfig.playerAConfig, "A", gameRunnerConfig, gameRunnerConfig.playerAId));
            players.add(parsePlayer(gameRunnerConfig.playerBConfig, "B", gameRunnerConfig, gameRunnerConfig.playerBId));
        }


        return players;
    }

    private Player parsePlayer(String playerConfig, String playerNumber, GameRunnerConfig gameRunnerConfig, String playerId) throws Exception {
        return parsePlayer(playerConfig, playerNumber, gameRunnerConfig, playerId, null, -1);
    }

    private Player parsePlayer(String playerConfig, String playerNumber, GameRunnerConfig gameRunnerConfig, String playerId, File botZip, int apiPort) throws Exception {

        BasePlayer player;

        if (playerConfig.equals("console")) {
            player = new ConsolePlayer(String.format("BotPlayer %s", playerNumber));
        } else {
            LOGGER.info("Config for player {} : {}", playerNumber, playerConfig);
            BotMetaData botConfig = BotMetaData.load(playerConfig);

            if (gameRunnerConfig.isTournamentMode)
                player = new TournamentPlayer(gameRunnerConfig, String.format("%s - %s", playerNumber, botConfig.getNickName()), apiPort, botZip);
            else {
                LOGGER.info(botConfig.getBotLocation());
                File botFile = new File(botConfig.getBotDirectory());
                if (!botFile.exists()) {
                    throw new FileNotFoundException(String.format("Could not find %s bot file for %s(%s)", botConfig.getBotLanguage(), botConfig.getAuthor(), botConfig.getNickName()));
                }

                BotRunner botRunner = BotRunnerFactory.createBotRunner(botConfig, gameRunnerConfig.maximumBotRuntimeMilliSeconds);
                player = new BotPlayer(String.format("%s - %s", playerNumber, botConfig.getNickName()), botRunner);
            }
        }

        player.setPlayerId(playerId);
        return player;
    }
}
