package za.co.entelect.challenge.bootstrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import za.co.entelect.challenge.botrunners.BotRunner;
import za.co.entelect.challenge.botrunners.BotRunnerFactory;
import za.co.entelect.challenge.core.engine.TowerDefenseGameEngine;
import za.co.entelect.challenge.core.engine.TowerDefenseGameMapGenerator;
import za.co.entelect.challenge.core.engine.TowerDefenseRoundProcessor;
import za.co.entelect.challenge.engine.runner.GameEngineRunner;
import za.co.entelect.challenge.entities.BotMetaData;
import za.co.entelect.challenge.game.contracts.game.GameResult;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.player.BotPlayer;
import za.co.entelect.challenge.player.ConsolePlayer;
import za.co.entelect.challenge.player.TournamentPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameBootstrapper {

    private static final Logger log = LogManager.getLogger(GameBootstrapper.class);

    public static void main(String[] args) {
        new GameBootstrapper().run(args);
    }

    private GameResult run(String[] args) {

        try {
            Config config = Config.load("./game-runner-config.json", args);

            initLogging(config);
            List<Player> players = loadPlayers(config);

            GameEngineRunner engineRunner = new GameEngineRunner.Builder()
                    .setConfig(config)
                    .setGameEngine(new TowerDefenseGameEngine(config.gameConfigFileLocation))
                    .setGameMapGenerator(new TowerDefenseGameMapGenerator())
                    .setRoundProcessor(new TowerDefenseRoundProcessor())
                    .setPlayers(players)
                    .build();

            engineRunner.runMatch();

        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }

    private void initLogging(Config config) throws Exception {

        if (config.isVerbose) {
            Configurator.setRootLevel(Level.DEBUG);
        } else {
            Configurator.setRootLevel(Level.ERROR);
        }
    }

    private List<Player> loadPlayers(Config config) throws Exception {
        List<Player> players = new ArrayList<>();

        players.add(parsePlayer(config.playerAConfig, "A", config));
        players.add(parsePlayer(config.playerBConfig, "B", config));

        return players;
    }

    private Player parsePlayer(String playerConfig, String playerNumber, Config config) throws Exception {
        if (playerConfig.equals("console")) {
            return new ConsolePlayer(String.format("BotPlayer %s", playerNumber));
        } else {
            BotMetaData botConfig = getBotMetaData(playerConfig);
            BotRunner botRunner = BotRunnerFactory.createBotRunner(botConfig, config.maximumBotRuntimeMilliSeconds);

            File botFile = new File(botConfig.getBotDirectory());
            if (!botFile.exists()) {
                throw new FileNotFoundException(String.format("Could not find %s bot file for %s(%s)", botConfig.getBotLanguage(), botConfig.getAuthor(), botConfig.getNickName()));
            }

            if (config.isTournamentMode)
                return new TournamentPlayer(String.format("%s - %s", playerNumber, botConfig.getNickName()), botRunner, botConfig.getBotLanguage(), config.gameName);
            else
                return new BotPlayer(String.format("%s - %s", playerNumber, botConfig.getNickName()), botRunner, config.gameName);
        }
    }

    private BotMetaData getBotMetaData(String botLocation) throws Exception {

        Optional<Path> botMetaPath = Files.walk(Paths.get(botLocation))
                .filter(path -> path.endsWith("bot.json"))
                .findFirst();

        if (!botMetaPath.isPresent()) {
            throw new Exception("Failed to find bot meta data from location: " + botLocation);
        }

        try (FileReader fileReader = new FileReader(botMetaPath.get().toFile())) {

            Gson gson = new GsonBuilder().create();

            BotMetaData botMeta = gson.fromJson(fileReader, BotMetaData.class);
            botMeta.setRelativeBotLocation(botLocation);

            return botMeta;
        }
    }
}
