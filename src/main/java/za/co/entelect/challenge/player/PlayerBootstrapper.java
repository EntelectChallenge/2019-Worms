package za.co.entelect.challenge.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.botrunners.BotRunner;
import za.co.entelect.challenge.botrunners.BotRunnerFactory;
import za.co.entelect.challenge.config.BotMetaData;
import za.co.entelect.challenge.config.GameRunnerConfig;
import za.co.entelect.challenge.game.contracts.player.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class PlayerBootstrapper {

    private static final Logger LOGGER = LogManager.getLogger(PlayerBootstrapper.class);

    public List<Player> loadPlayers(GameRunnerConfig gameRunnerConfig) throws Exception {
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
