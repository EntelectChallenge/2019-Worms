package za.co.entelect.challenge.bootstrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.util.TriConsumer;
import za.co.entelect.challenge.botrunners.BotRunner;
import za.co.entelect.challenge.botrunners.BotRunnerFactory;
import za.co.entelect.challenge.core.engine.TowerDefenseGameEngine;
import za.co.entelect.challenge.core.engine.TowerDefenseGameMapGenerator;
import za.co.entelect.challenge.core.engine.TowerDefenseRoundProcessor;
import za.co.entelect.challenge.engine.runner.GameEngineRunner;
import za.co.entelect.challenge.entities.BotMetaData;
import za.co.entelect.challenge.game.contracts.game.GamePlayer;
import za.co.entelect.challenge.game.contracts.game.GameResult;
import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.player.BotPlayer;
import za.co.entelect.challenge.player.ConsolePlayer;
import za.co.entelect.challenge.player.TournamentPlayer;
import za.co.entelect.challenge.utils.FileUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class GameBootstrapper {

    private static final Logger log = LogManager.getLogger(GameBootstrapper.class);

    private GameEngineRunner gameEngineRunner;
    private static String gameName;

    public static void main(String[] args) {
        new GameBootstrapper().run(args);
    }

    public GameResult run(String[] args) {

        Config config = null;
        try {
            config = loadConfig(args);

            prepareEngineRunner(config);
            prepareHandlers();
            prepareGame(config);

            startGame();

        } catch (Exception e) {
            log.error(e);
            gameEngineRunner.setMatchSuccess(false);
        }

        return gameEngineRunner.getGameResult();
    }

    private Config loadConfig(String[] args) throws Exception {
        try (FileReader fileReader = new FileReader("./game-runner-config.json")) {
            Gson gson = new GsonBuilder().create();
            Config config = gson.fromJson(fileReader, Config.class);

            if (config == null)
                throw new Exception("Failed to load config");

            if (config.isTournamentMode) {

                if (args.length != 2)
                    throw new Exception("No bot locations specified for tournament");

                config.playerAConfig = args[0];
                config.playerBConfig = args[1];
            }

            return config;
        }
    }

    private void prepareEngineRunner(Config config) {
        gameEngineRunner = new GameEngineRunner();

        gameEngineRunner.setGameEngine(new TowerDefenseGameEngine(config.gameConfigFileLocation));
        gameEngineRunner.setGameMapGenerator(new TowerDefenseGameMapGenerator());
        gameEngineRunner.setGameRoundProcessor(new TowerDefenseRoundProcessor());
    }

    private void prepareHandlers() {
        gameEngineRunner.firstPhaseHandler = getFirstPhaseHandler();
        gameEngineRunner.gameStartedHandler = getGameStartedHandler();
        gameEngineRunner.gameCompleteHandler = getGameCompleteHandler();
        gameEngineRunner.roundStartingHandler = getRoundStartingHandler();
        gameEngineRunner.roundCompleteHandler = getRoundCompleteHandler();
    }

    private void prepareGame(Config config) throws Exception {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        gameName = FileUtils.getAbsolutePath(config.roundStateOutputLocation) + "/" + timeStamp;

        List<Player> players = new ArrayList<>();

        players.add(parsePlayer(config.playerAConfig, "A", config));
        players.add(parsePlayer(config.playerBConfig, "B", config));

        gameEngineRunner.preparePlayers(players);
        gameEngineRunner.prepareGameMap();

        if (config.isVerbose) {
            Configurator.setRootLevel(Level.DEBUG);
        } else {
            Configurator.setRootLevel(Level.ERROR);
        }
    }

    private Player parsePlayer(String playerConfig, String playerNumber, Config config) throws Exception {
        if (playerConfig.equals("console")) {
            return new ConsolePlayer(String.format("Player %s", playerNumber));
        } else {
            BotMetaData botConfig = getBotMetaData(playerConfig);
            BotRunner botRunner = BotRunnerFactory.createBotRunner(botConfig, config.maximumBotRuntimeMilliSeconds);

            File botFile = new File(botConfig.getBotDirectory());
            if (!botFile.exists()) {
                throw new FileNotFoundException(String.format("Could not find %s bot file for %s(%s)", botConfig.getBotLanguage(), botConfig.getAuthor(), botConfig.getNickName()));
            }

            if(config.isTournamentMode)
                return new TournamentPlayer(String.format("%s - %s", playerNumber, botConfig.getNickName()), botRunner, botConfig.getBotLanguage(), gameName);
            else
                return new BotPlayer(String.format("%s - %s", playerNumber, botConfig.getNickName()), botRunner, gameName);
        }
    }

    private BotMetaData getBotMetaData(String botLocation) throws Exception {
        try (FileReader fileReader = new FileReader(String.format("%s/bot.json", botLocation))) {

            Gson gson = new GsonBuilder().create();

            BotMetaData botMeta = gson.fromJson(fileReader, BotMetaData.class);

            botMeta.setRelativeBotLocation(botLocation);

            if (botMeta == null)
                throw new Exception("Failed to load bot meta data from location: " + botLocation);

            return botMeta;
        }
    }

    private void startGame() throws Exception {
        gameEngineRunner.startNewGame();
    }

    private Consumer<GameMap> getFirstPhaseHandler() {
        return gameMap -> {
        };
    }

    private BiConsumer<GameMap, Integer> getRoundCompleteHandler() {
        return (gameMap, round) -> {
            log.info("=======================================");
            log.info("Round ended " + round);
            log.info("=======================================");
        };
    }

    private TriConsumer<GameMap, List<Player>, Boolean> getGameCompleteHandler() {
        return (gameMap, players, matchSuccessful) -> {
            GamePlayer winningPlayer = gameMap.getWinningPlayer();

            Player winner = players.stream()
                    .filter(p -> p.getGamePlayer() == winningPlayer)
                    .findFirst().orElse(null);

            StringBuilder winnerStringBuilder = new StringBuilder();

            for (Player player : players) {
                winnerStringBuilder.append(player.getName()
                        + "- score:" + player.getGamePlayer().getScore()
                        + " health:" + player.getGamePlayer().getHealth()
                        + "\n");
            }

            log.info("=======================================");
            log.info((winner == null)
                    ? "The game ended in a tie"
                    : "The winner is: " + winner.getName());
            log.info("=======================================");

            try {
                String roundLocation = String.format("%s/%s/endGameState.txt", gameName, FileUtils.getRoundDirectory(gameMap.getCurrentRound()));
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(roundLocation)));

                if (winner == null) {
                    winnerStringBuilder.insert(0, "The game ended in a tie" + "\n\n");
                } else {
                    winnerStringBuilder.insert(0, "The winner is: " + winner.getName() + "\n\n");
                }

                if (!matchSuccessful) {
                    winnerStringBuilder.insert(0, "Bot did nothing too many consecutive rounds" + "\n\n");
                }

                bufferedWriter.write(winnerStringBuilder.toString());
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private BiFunction<GameMap, Integer, String> getRoundStartingHandler() {
        return (gameMap, round) -> {
            StringBuilder s = new StringBuilder();
            s.append("=======================================" + "\n");
            s.append("Starting round " + round + "\n");
            s.append("=======================================" + "\n");
            return s.toString();
        };
    }

    private Function<GameMap, String> getGameStartedHandler() {
        return gameMap -> {
            StringBuilder s = new StringBuilder();
            s.append("=======================================" + "\n");
            s.append("Starting game" + "\n");
            s.append("=======================================" + "\n");
            return s.toString();
        };
    }
}
