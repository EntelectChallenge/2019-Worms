package za.co.entelect.challenge.player;

import net.lingala.zip4j.core.ZipFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.botrunners.BotRunner;
import za.co.entelect.challenge.botrunners.BotRunnerFactory;
import za.co.entelect.challenge.config.BotMetaData;
import za.co.entelect.challenge.config.GameRunnerConfig;
import za.co.entelect.challenge.config.TournamentConfig;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.storage.AzureBlobStorageService;
import za.co.entelect.challenge.utils.EnvironmentVariable;

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
            AzureBlobStorageService storageService = new AzureBlobStorageService(tournamentConfig.connectionString, tournamentConfig.botsContainer);
            File playerAZip = storageService.getFile(playerAEnv, String.format("./tournament-tmp/%s.zip", UUID.randomUUID()));
            File playerBZip = storageService.getFile(playerBEnv, String.format("./tournament-tmp/%s.zip", UUID.randomUUID()));

            gameRunnerConfig.playerAConfig = extractBot(playerAZip).getPath();
            gameRunnerConfig.playerBConfig = extractBot(playerBZip).getPath();
        }

        players.add(parsePlayer(gameRunnerConfig.playerAConfig, "A", gameRunnerConfig));
        players.add(parsePlayer(gameRunnerConfig.playerBConfig, "B", gameRunnerConfig));

        return players;
    }

    private Player parsePlayer(String playerConfig, String playerNumber, GameRunnerConfig gameRunnerConfig) throws Exception {

        BasePlayer player;

        if (playerConfig.equals("console")) {
            player = new ConsolePlayer(String.format("BotPlayer %s", playerNumber));
        } else {
            BotMetaData botConfig = BotMetaData.load(playerConfig);
            BotRunner botRunner = BotRunnerFactory.createBotRunner(botConfig, gameRunnerConfig.maximumBotRuntimeMilliSeconds);

            File botFile = new File(botConfig.getBotDirectory());
            if (!botFile.exists()) {
                throw new FileNotFoundException(String.format("Could not find %s bot file for %s(%s)", botConfig.getBotLanguage(), botConfig.getAuthor(), botConfig.getNickName()));
            }

            player = new BotPlayer(String.format("%s - %s", playerNumber, botConfig.getNickName()), botRunner);

//            if (gameRunnerConfig.isTournamentMode)
//                return new TournamentPlayer(String.format("%s - %s", playerNumber, botConfig.getNickName()), botRunner, botConfig.getBotLanguage());
//            else
//                return new BotPlayer(String.format("%s - %s", playerNumber, botConfig.getNickName()), botRunner);
        }

        player.setPlayerId(UUID.randomUUID().toString());
        return player;
    }

    private File extractBot(File botZip) throws Exception {

        LOGGER.info(String.format("Extracting bot: %s", botZip.getName()));
        ZipFile zipFile = new ZipFile(botZip);

        String extractFilePath = String.format("%s/extracted/%s", "tournament-tmp", botZip.getName().replace(".zip", ""));
        File extractedFile = new File(extractFilePath);

        zipFile.extractAll(extractedFile.getCanonicalPath());

        // Select the actual bot folder located inside the extraction folder container
        extractedFile = extractedFile.listFiles()[0];

        return extractedFile;
    }
}
