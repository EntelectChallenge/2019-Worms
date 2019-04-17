package za.co.entelect.challenge.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import za.co.entelect.challenge.utils.EnvironmentVariable;
import za.co.entelect.challenge.utils.FileUtils;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GameRunnerConfig {

    private static final transient Logger LOGGER = LogManager.getLogger(GameRunnerConfig.class);

    @SerializedName("game-name")
    public String gameName;

    @SerializedName("game-engine-jar")
    public String gameEngineJar;

    @SerializedName("player-a")
    public String playerAConfig;

    @SerializedName("player-b")
    public String playerBConfig;

    @SerializedName("player-a-id")
    public String playerAId;

    @SerializedName("player-b-id")
    public String playerBId;

    @SerializedName("max-runtime-ms")
    public int maximumBotRuntimeMilliSeconds;

    @SerializedName("round-state-output-location")
    public String roundStateOutputLocation;

    @SerializedName("game-config-file-location")
    public String gameConfigFileLocation;

    @SerializedName("verbose-mode")
    public boolean isVerbose;

    @SerializedName("match-id")
    public String matchId;

    @SerializedName("seed")
    public int seed;

    @SerializedName("max-request-retries")
    public int maxRequestRetries;

    @SerializedName("request-timeout-ms")
    public int requestTimeout;

    @SerializedName("is-tournament-mode")
    public boolean isTournamentMode;

    @SerializedName("tournament")
    public TournamentConfig tournamentConfig;

    public static GameRunnerConfig load(String configFile) throws Exception {

        LOGGER.info(String.format("Reading config file: %s", configFile));
        try (FileReader fileReader = new FileReader(configFile)) {
            Gson gson = new GsonBuilder().create();
            GameRunnerConfig gameRunnerConfig = gson.fromJson(fileReader, GameRunnerConfig.class);

            if (gameRunnerConfig == null) {
                throw new Exception("Failed to load gameRunnerConfig");
            }

            // Load tournament specific config here
            // Bot locations are dynamic, therefore, it's set in the PlayerBootstrapper
            if (gameRunnerConfig.isTournamentMode) {
                LOGGER.info("Running in tournament mode. Loading tournament config");
                gameRunnerConfig.matchId = System.getenv(EnvironmentVariable.MATCH_ID.name());
                gameRunnerConfig.seed = Integer.valueOf(System.getenv(EnvironmentVariable.SEED.name()));
                gameRunnerConfig.playerAId = System.getenv(EnvironmentVariable.PLAYER_A_ID.name());
                gameRunnerConfig.playerBId = System.getenv(EnvironmentVariable.PLAYER_B_ID.name());
            }

            if (gameRunnerConfig.matchId == null) {
                LOGGER.info("No match id found. Generating one");
                gameRunnerConfig.matchId = UUID.randomUUID().toString();
            }

            if (gameRunnerConfig.gameName == null || gameRunnerConfig.gameName.isEmpty()) {
                LOGGER.info("Building game name");
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                gameRunnerConfig.gameName = FileUtils.getAbsolutePath(gameRunnerConfig.roundStateOutputLocation) + "/" + timeStamp;
            }

            if (gameRunnerConfig.playerAId == null || gameRunnerConfig.playerAId.isEmpty()) {
                LOGGER.warn("Player A id not found. Generating one");
                gameRunnerConfig.playerAId = UUID.randomUUID().toString();
            }

            if (gameRunnerConfig.playerBId == null || gameRunnerConfig.playerBId.isEmpty()) {
                LOGGER.warn("Player B id not found. Generating one");
                gameRunnerConfig.playerBId = UUID.randomUUID().toString();
            }

            // If the seed is 0 then it's not initialized
            if (gameRunnerConfig.seed == 0) {
                gameRunnerConfig.seed = (int) TimeUnit.SECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
            }
            LOGGER.info(String.format("Match will be running with a seed of: %d", gameRunnerConfig.seed));

            return gameRunnerConfig;
        }
    }
}
