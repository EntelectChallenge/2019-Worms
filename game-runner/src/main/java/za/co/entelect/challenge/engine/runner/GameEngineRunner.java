package za.co.entelect.challenge.engine.runner;

import io.reactivex.subjects.BehaviorSubject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.config.GameRunnerConfig;
import za.co.entelect.challenge.engine.exceptions.InvalidRunnerState;
import za.co.entelect.challenge.game.contracts.command.RawCommand;
import za.co.entelect.challenge.game.contracts.common.RefereeMessage;
import za.co.entelect.challenge.game.contracts.exceptions.TimeoutException;
import za.co.entelect.challenge.game.contracts.game.*;
import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.game.contracts.renderer.GameMapRenderer;
import za.co.entelect.challenge.game.contracts.renderer.RendererType;
import za.co.entelect.challenge.player.entity.BasePlayer;
import za.co.entelect.challenge.player.entity.BotExecutionContext;
import za.co.entelect.challenge.renderer.RendererResolver;
import za.co.entelect.challenge.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameEngineRunner implements LifecycleEngineRunner {

    private static final Logger log = LogManager.getLogger(GameEngineRunner.class);

    private GameRunnerConfig gameRunnerConfig;

    private String consoleOutput = "";
    private BehaviorSubject<Boolean> unsubscribe;
    private BehaviorSubject<String> addToConsoleOutput;

    private GameMap gameMap;
    private List<Player> players;
    private RunnerReferee referee;
    private RunnerRoundProcessor roundProcessor;

    private GameResult gameResult;
    private GameEngine gameEngine;
    private GameMapGenerator gameMapGenerator;
    private GameRoundProcessor gameRoundProcessor;

    private RendererResolver rendererResolver;
    private List<BotExecutionContext> botExecutionContexts;

    public GameResult runMatch() throws Exception {

        onGameStarting();
        while (!isGameComplete()) {
            onRoundStarting();
            onProcessRound();
            onRoundComplete();
        }
        onGameComplete();

        return gameResult;
    }

    @Override
    public void onGameStarting() throws Exception {

        this.unsubscribe = BehaviorSubject.create();
        this.addToConsoleOutput = BehaviorSubject.create();
        this.addToConsoleOutput
                .takeUntil(this.unsubscribe)
                .subscribe(text -> consoleOutput += text);

        if (players == null || players.size() == 0) {
            throw new InvalidRunnerState("No players provided");
        }

        prepareGameMap();
        preparePlayers();

        StringBuilder s = new StringBuilder();
        s.append("=======================================\n");
        s.append("Starting game\n");
        s.append("=======================================\n");

        log.info(s);

        gameResult = new GameResult();
        gameResult.isComplete = false;
        gameResult.verificationRequired = false;
        gameResult.matchId = gameRunnerConfig.matchId;

        botExecutionContexts = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void onRoundStarting() {
        gameMap.setCurrentRound(gameMap.getCurrentRound() + 1);

        StringBuilder s = new StringBuilder();
        s.append("=======================================\n");
        s.append(String.format("Starting round: %d \n", gameMap.getCurrentRound()));
        s.append("=======================================\n");

        log.info(s);

        roundProcessor = new RunnerRoundProcessor(gameMap, gameRoundProcessor);

        botExecutionContexts.clear();
    }

    @Override
    public void onProcessRound() throws Exception {
        GameMapRenderer renderer = rendererResolver.resolve(RendererType.CONSOLE);
        log.info(() -> {
            if (!gameRunnerConfig.isTournamentMode) {
                return renderer.render(gameMap, players.get(0).getGamePlayer());
            }

            return "";
        });

        for (Player player : players) {

            Thread thread = new Thread(() -> {
                BasePlayer currentPlayer = (BasePlayer) player;
                BotExecutionContext botExecutionContext = currentPlayer.executeBot(gameMap);

                botExecutionContexts.add(botExecutionContext);
                roundProcessor.addPlayerCommand(player, new RawCommand(botExecutionContext.command));
                referee.trackExecution(player, botExecutionContext);
            });
            thread.start();
            thread.join();
        }
        roundProcessor.processRound();
        players.forEach(p -> p.roundComplete(gameMap, gameMap.getCurrentRound()));
    }

    @Override
    public void onRoundComplete() {
        StringBuilder s = new StringBuilder();
        s.append("=======================================\n");
        s.append(String.format("Completed round: %d \n", gameMap.getCurrentRound()));
        s.append("=======================================\n");

        log.info(s);

        for (BotExecutionContext botExecutionContext : botExecutionContexts) {
            try {
                botExecutionContext.saveRoundStateData(gameRunnerConfig.gameName);
            } catch (Exception e) {
                log.error("Failed to write round information", e);
            }
        }
    }

    @Override
    public void onGameComplete() {
        this.unsubscribe.onNext(Boolean.TRUE);

        GamePlayer winningPlayer = gameMap.getWinningPlayer();
        BasePlayer winner = players.stream()
                .map(player -> (BasePlayer) player)
                .filter(p -> p.getGamePlayer().equals(winningPlayer))
                .findFirst().orElse(null);

        if (winner != null) {
            gameResult.winner = winner.getPlayerId();
        }

        gameResult.playerAId = gameRunnerConfig.playerAId;
        gameResult.playerBId = gameRunnerConfig.playerBId;

        for (Player player : players) {
            BasePlayer basePlayer = (BasePlayer) player;
            if (basePlayer.getPlayerId().equals(gameRunnerConfig.playerAId)) {
                gameResult.playerOnePoints = basePlayer.getGamePlayer().getScore();
            } else if (basePlayer.getPlayerId().equals(gameRunnerConfig.playerBId)) {
                gameResult.playerTwoPoints = basePlayer.getGamePlayer().getScore();
            }
        }

        gameResult.roundsPlayed = gameMap.getCurrentRound();
        gameResult.isComplete = true;
        gameResult.isSuccessful = true;

        RefereeMessage refereeMessage = referee.isMatchValid(gameMap);
        gameResult.verificationRequired = !refereeMessage.isValid;

        writeEndGameFile(winner, refereeMessage);

        for (Player player : players) {
            player.gameEnded(gameMap);
        }
    }

    private boolean isGameComplete() throws TimeoutException {
        return gameEngine.isGameComplete(gameMap);
    }

    private void writeEndGameFile(Player winner, RefereeMessage refereeMessage) {
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
            String roundLocation = String.format("%s/%s/endGameState.txt", gameRunnerConfig.gameName, FileUtils.getRoundDirectory(gameMap.getCurrentRound()));
            File endStateFile = new File(roundLocation);

            if (!endStateFile.getParentFile().exists()) {
                endStateFile.getParentFile().mkdirs();
            }

            if (!endStateFile.exists()) {
                endStateFile.createNewFile();
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(endStateFile));

            if (winner == null) {
                winnerStringBuilder.insert(0, "The game ended in a tie" + "\n\n");
            } else {
                winnerStringBuilder.insert(0, "The winner is: " + winner.getName() + "\n\n");
            }

            winnerStringBuilder.insert(0, "Match seed: " + gameRunnerConfig.seed + "\n\n");

            if (!refereeMessage.isValid) {
                winnerStringBuilder.append("=======================================\n");
                winnerStringBuilder.append("Referee messages\n");
                winnerStringBuilder.append("=======================================\n");

                for (String reason : refereeMessage.reasons) {
                    winnerStringBuilder.append(reason);
                    winnerStringBuilder.append("\n");
                }
            }

            bufferedWriter.write(winnerStringBuilder.toString());
            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareGameMap() throws InvalidRunnerState {

        if (gameMapGenerator == null) {
            throw new InvalidRunnerState("No GameMapGenerator instance found");
        }

        if (players == null || players.size() == 0) {
            throw new InvalidRunnerState("No players found");
        }

        gameMap = gameMapGenerator.generateGameMap(players);
    }

    private void preparePlayers() throws Exception {
        for (Player player : players) {
            ((BasePlayer) player).instantiateRenderers(rendererResolver);
            ((BasePlayer) player).gameStarted();
        }
    }

    public static class Builder {

        GameRunnerConfig gameRunnerConfig;
        GameEngine gameEngine;
        GameMapGenerator gameMapGenerator;
        GameRoundProcessor roundProcessor;
        List<Player> players;
        GameReferee referee;
        RendererResolver rendererResolver;

        public Builder setGameRunnerConfig(GameRunnerConfig gameRunnerConfig) {
            this.gameRunnerConfig = gameRunnerConfig;
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

        public Builder setRendererResolver(RendererResolver rendererFactory) {
            this.rendererResolver = rendererFactory;
            return this;
        }

        public Builder setReferee(GameReferee referee) {
            this.referee = referee;
            return this;
        }

        public GameEngineRunner build() {
            GameEngineRunner gameEngineRunner = new GameEngineRunner();

            gameEngineRunner.gameRunnerConfig = gameRunnerConfig;
            gameEngineRunner.gameEngine = gameEngine;
            gameEngineRunner.gameMapGenerator = gameMapGenerator;
            gameEngineRunner.gameRoundProcessor = roundProcessor;
            gameEngineRunner.players = players;
            gameEngineRunner.referee = new RunnerReferee(referee, gameRunnerConfig, players);
            gameEngineRunner.rendererResolver = rendererResolver;

            return gameEngineRunner;
        }
    }
}

