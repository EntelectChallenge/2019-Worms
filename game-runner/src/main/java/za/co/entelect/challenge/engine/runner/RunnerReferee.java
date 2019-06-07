package za.co.entelect.challenge.engine.runner;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.config.GameRunnerConfig;
import za.co.entelect.challenge.game.contracts.common.RefereeMessage;
import za.co.entelect.challenge.game.contracts.game.GameReferee;
import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.player.entity.BasePlayer;
import za.co.entelect.challenge.player.entity.BotExecutionContext;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class RunnerReferee implements GameReferee {

    private static final Logger log = LogManager.getLogger(RunnerReferee.class);


    private GameReferee gameEngineReferee;
    private GameRunnerConfig gameRunnerConfig;
    private Hashtable<Player, List<Integer>> playerTimeouts;
    private Hashtable<Player, List<Integer>> playerExceptions;

    RunnerReferee(GameReferee gameEngineReferee, GameRunnerConfig gameRunnerConfig, List<Player> players) {
        this.gameEngineReferee = gameEngineReferee;
        this.gameRunnerConfig = gameRunnerConfig;

        this.playerTimeouts = new Hashtable<>();
        this.playerExceptions = new Hashtable<>();

        for (Player player : players) {
            this.playerTimeouts.put(player, new ArrayList<>());
            this.playerExceptions.put(player, new ArrayList<>());
        }
    }

    @Override
    public RefereeMessage isMatchValid(GameMap gameMap) {

        log.info("Check if match is valid");
        List<String> errorList = new ArrayList<>();
        for (Player player : playerTimeouts.keySet()) {
            BasePlayer basePlayer = (BasePlayer) player;
            List<Integer> timeouts = playerTimeouts.get(player);

            // Compare the current round to the previous
            for (int i = 1; i < timeouts.size(); i++) {
                int previousRound = timeouts.get(i - 1);
                int currentRound = timeouts.get(i);
                if (previousRound == currentRound - 1) {
                    errorList.add(String.format("Player: %s (%s) -> Consecutive timeouts on rounds %d and %d", basePlayer.getPlayerId(), basePlayer.getName(), previousRound, currentRound));
                }
            }
        }

        for (Player player : playerExceptions.keySet()) {
            BasePlayer basePlayer = (BasePlayer) player;
            List<Integer> exceptions = playerExceptions.get(player);

            // Compare the current round to the previous
            for (int i = 1; i < exceptions.size(); i++) {
                int previousRound = exceptions.get(i - 1);
                int currentRound = exceptions.get(i);
                if (previousRound == currentRound - 1) {
                    errorList.add(String.format("Player: %s (%s) -> Consecutive exceptions on rounds %d and %d", basePlayer.getPlayerId(), basePlayer.getName(), previousRound, currentRound));
                }
            }
        }

        for (String s : errorList) {
            log.info(s);
        }

        RefereeMessage gameEngineCheck = gameEngineReferee.isMatchValid(gameMap);
        gameEngineCheck.isValid = gameEngineCheck.isValid && errorList.isEmpty();
        gameEngineCheck.reasons.addAll(errorList);

        return gameEngineCheck;
    }

    void trackExecution(Player player, BotExecutionContext botExecutionContext) {
        trackTimeouts(player, botExecutionContext);
        trackExceptions(player, botExecutionContext);
    }

    private void trackTimeouts(Player player, BotExecutionContext botExecutionContext) {
        if (botExecutionContext.executionTime >= gameRunnerConfig.maximumBotRuntimeMilliSeconds) {
            List<Integer> timeouts = playerTimeouts.get(player);
            timeouts.add(botExecutionContext.round);
        }
    }

    private void trackExceptions(Player player, BotExecutionContext botExecutionContext) {
        if (!StringUtils.isEmpty(botExecutionContext.exception)) {
            List<Integer> exceptions = playerExceptions.get(player);
            exceptions.add(botExecutionContext.round);
        }
    }
}
