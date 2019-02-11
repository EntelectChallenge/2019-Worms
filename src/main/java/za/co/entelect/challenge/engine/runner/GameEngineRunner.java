package za.co.entelect.challenge.engine.runner;

import io.reactivex.subjects.BehaviorSubject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.config.Config;
import za.co.entelect.challenge.config.GameConfig;
import za.co.entelect.challenge.core.renderers.TowerDefenseConsoleMapRenderer;
import za.co.entelect.challenge.engine.exceptions.InvalidRunnerState;
import za.co.entelect.challenge.game.contracts.command.RawCommand;
import za.co.entelect.challenge.game.contracts.exceptions.MatchFailedException;
import za.co.entelect.challenge.game.contracts.exceptions.TimeoutException;
import za.co.entelect.challenge.game.contracts.game.*;
import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

public class GameEngineRunner implements LifecycleEngineRunner {

    private static final Logger log = LogManager.getLogger(GameEngineRunner.class);

    private Config config;

    private String consoleOutput = "";
    private BehaviorSubject<String> addToConsoleOutput;
    private BehaviorSubject<Boolean> unsubscribe;

    private GameMap gameMap;
    private List<Player> players;
    private RunnerRoundProcessor roundProcessor;

    private GameEngine gameEngine;
    private GameMapGenerator gameMapGenerator;
    private GameRoundProcessor gameRoundProcessor;
    private GameResult gameResult;

    public GameEngineRunner() {
        this.unsubscribe = BehaviorSubject.create();
        this.addToConsoleOutput = BehaviorSubject.create();
        this.addToConsoleOutput
                .takeUntil(this.unsubscribe)
                .subscribe(text -> consoleOutput += text);
    }

    public void runMatch() throws Exception {

        onGameStarting();
        while (!gameResult.isComplete) {
            onRoundStarting();
            onProcessRound();
            onRoundComplete();
        }

        onGameComplete();
    }

    @Override
    public void onGameStarting() throws Exception {

        prepareGameMap();
        if (gameMap == null) {
            throw new InvalidRunnerState("Game has not yet been prepared");
        }

        preparePlayers();

        StringBuilder s = new StringBuilder();
        s.append("=======================================\n");
        s.append("Starting game\n");
        s.append("=======================================\n");

        log.info(s);

        gameResult = new GameResult();
        gameResult.isComplete = false;
        gameResult.verificationRequired = false;
    }

    @Override
    public void onRoundStarting() {
        StringBuilder s = new StringBuilder();
        s.append("=======================================\n");
        s.append(String.format("Starting round: %d \n", gameMap.getCurrentRound()));
        s.append("=======================================\n");

        log.info(s);

        roundProcessor = new RunnerRoundProcessor(gameMap, gameRoundProcessor);
        addToConsoleOutput.onNext(s.toString());
    }

    @Override
    public void onProcessRound() throws Exception {
        processRound();
    }

    @Override
    public void onRoundComplete() {
        StringBuilder s = new StringBuilder();
        s.append("=======================================\n");
        s.append(String.format("Completed round: %d \n", gameMap.getCurrentRound()));
        s.append("=======================================\n");

        log.info(s);
    }

    @Override
    public void onGameComplete() {
        this.unsubscribe.onNext(Boolean.TRUE);

        GamePlayer winningPlayer = gameMap.getWinningPlayer();

        za.co.entelect.challenge.game.contracts.player.Player winner = players.stream()
                .filter(p -> p.getGamePlayer() == winningPlayer)
                .findFirst().orElse(null);

        StringBuilder winnerStringBuilder = new StringBuilder();

        for (za.co.entelect.challenge.game.contracts.player.Player player : players) {
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
            String roundLocation = String.format("%s/%s/endGameState.txt", config.gameName, FileUtils.getRoundDirectory(gameMap.getCurrentRound()));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(roundLocation)));

            if (winner == null) {
                winnerStringBuilder.insert(0, "The game ended in a tie" + "\n\n");
            } else {
                winnerStringBuilder.insert(0, "The winner is: " + winner.getName() + "\n\n");
            }

//            if (!matchSuccessful) {
//                winnerStringBuilder.insert(0, "Bot did nothing too many consecutive rounds" + "\n\n");
//            }

            bufferedWriter.write(winnerStringBuilder.toString());
            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processRound() throws Exception {
        TowerDefenseConsoleMapRenderer renderer = new TowerDefenseConsoleMapRenderer();

        // Only execute the render if the log mode is in INFO.
        log.info(() -> {
            String consoleText = consoleOutput + renderer.render(gameMap, players.get(0).getGamePlayer());
            consoleOutput = "";

            return consoleText;
        });

        gameMap.setCurrentRound(gameMap.getCurrentRound() + 1);

        try {
            if (gameEngine.isGameComplete(gameMap)) {
                publishGameComplete(true);
                return;
            }
        } catch (TimeoutException e) {
            publishGameComplete(false);
            return;
        }

        for (Player player : players) {
            Thread thread = new Thread(() -> player.newRoundStarted(gameMap));
            thread.start();
            thread.join();
        }

        roundProcessor.processRound(addToConsoleOutput);
        players.forEach(p -> p.roundComplete(gameMap, gameMap.getCurrentRound()));
    }

    private void preparePlayers() throws InvalidRunnerState {

        if (players == null || players.size() == 0)
            throw new InvalidRunnerState("No players provided");

        for (Player player : players) {
            player.publishCommandHandler = getPlayerCommandListener();
        }
    }

    private void prepareGameMap() throws InvalidRunnerState {

        if (gameMapGenerator == null)
            throw new InvalidRunnerState("No GameMapGenerator instance found");

        if (players == null || players.size() == 0)
            throw new InvalidRunnerState("No players found");

        gameMap = gameMapGenerator.generateGameMap(players);
    }

    private BiConsumer<Player, RawCommand> getPlayerCommandListener() {
        return (player, command) -> roundProcessor.addPlayerCommand(player, command);
    }

    private void publishGameComplete(boolean matchDidNotTimeout) throws Exception {
        GamePlayer winningPlayer = gameMap.getWinningPlayer();

        gameResult.winner = 0;

        for (Player player : players) {
            player.gameEnded(gameMap);

            int score = player.getGamePlayer().getScore();

            if (player.getName().substring(0, 1).equals("A")) {
                gameResult.playerOnePoints = score;

                if (winningPlayer != null && winningPlayer.getScore() == score) {
                    gameResult.winner = 1;
                }
            } else {
                gameResult.playerTwoPoints = score;

                if (winningPlayer != null && winningPlayer.getScore() == score) {
                    gameResult.winner = 2;
                }
            }
        }

        gameResult.roundsPlayed = gameMap.getCurrentRound();
        gameResult.isComplete = true;
        gameResult.isSuccessful = matchDidNotTimeout;

        if (!matchDidNotTimeout) {
            gameResult.verificationRequired = true;
            throw new MatchFailedException("Match timed out");
        }

        int minExpectRounds = 36;
        if (gameResult.roundsPlayed < minExpectRounds) {
            gameResult.verificationRequired = true;
            throw new MatchFailedException("Match duration was " + gameResult.roundsPlayed +
                    " rounds, still less than the expected " + minExpectRounds + " rounds");
        }

        int minExpectScore = gameResult.roundsPlayed * GameConfig.getRoundIncomeEnergy() * GameConfig.getEnergyScoreMultiplier();
        if (gameResult.playerOnePoints <= minExpectScore) {
            gameResult.verificationRequired = true;
            throw new MatchFailedException("BotPlayer One scored only" + gameResult.playerOnePoints +
                    " points, not even the expected " + minExpectScore + " points");
        }
        if (gameResult.playerTwoPoints <= minExpectScore) {
            gameResult.verificationRequired = true;
            throw new MatchFailedException("BotPlayer Two scored only" + gameResult.playerTwoPoints +
                    " points, not even the expected " + minExpectScore + " points");
        }

        int minExpectTimeouts = 0;
        int playerOneTimeOuts = players.get(0).getTimeoutCounts();
        if (playerOneTimeOuts > minExpectTimeouts) {
            gameResult.verificationRequired = true;
            throw new MatchFailedException("BotPlayer One timed out " + playerOneTimeOuts + " times");
        }
        int playerTwoTimeOuts = players.get(1).getTimeoutCounts();
        if (playerTwoTimeOuts > minExpectTimeouts) {
            gameResult.verificationRequired = true;
            throw new MatchFailedException("BotPlayer Two timed out " + playerTwoTimeOuts + " times");
        }

    }

    public GamePlayer getWinningPlayer() {
        return gameMap.getWinningPlayer();
    }

    public GameResult getGameResult() {
        return gameResult;
    }

    public void setMatchSuccess(boolean status) {
        gameResult.isSuccessful = status;

        if (status) {
            gameResult.verificationRequired = true;
        }
    }

    public static class Builder {

        Config config;
        GameEngine gameEngine;
        GameMapGenerator gameMapGenerator;
        GameRoundProcessor roundProcessor;
        List<Player> players;

        public Builder setConfig(Config config) {
            this.config = config;
            return this;
        }

        public Builder setGameEngine(GameEngine gameEngine) {
            this.gameEngine = gameEngine;
            return this;
        }

        public Builder setGameMapGenerator(GameMapGenerator gameMapGenerator) {
            this.gameMapGenerator = gameMapGenerator;
            return this;
        }

        public Builder setRoundProcessor(GameRoundProcessor roundProcessor) {
            this.roundProcessor = roundProcessor;
            return this;
        }

        public Builder setPlayers(List<Player> players) {
            this.players = players;
            return this;
        }

        public GameEngineRunner build() {
            GameEngineRunner gameEngineRunner = new GameEngineRunner();

            gameEngineRunner.config = config;
            gameEngineRunner.gameEngine = gameEngine;
            gameEngineRunner.gameMapGenerator = gameMapGenerator;
            gameEngineRunner.gameRoundProcessor = roundProcessor;
            gameEngineRunner.players = players;

            return gameEngineRunner;
        }
    }
}

