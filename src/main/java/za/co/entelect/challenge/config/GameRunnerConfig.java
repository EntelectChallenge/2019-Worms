package za.co.entelect.challenge.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import za.co.entelect.challenge.utils.EnvironmentVariable;
import za.co.entelect.challenge.utils.FileUtils;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class GameRunnerConfig {

    @SerializedName("game-name")
    public String gameName;

    @SerializedName("game-engine-jar")
    public String gameEngineJar;

    @SerializedName("player-a")
    public String playerAConfig;

    @SerializedName("player-b")
    public String playerBConfig;

    @SerializedName("max-runtime-ms")
    public int maximumBotRuntimeMilliSeconds;

    @SerializedName("round-state-output-location")
    public String roundStateOutputLocation;

    @SerializedName("game-config-file-location")
    public String gameConfigFileLocation;

    @SerializedName("verbose-mode")
    public boolean isVerbose;

    @SerializedName("is-tournament-mode")
    public boolean isTournamentMode;

    @SerializedName("match-id")
    public String matchId;

    public static GameRunnerConfig load(String configFile) throws Exception {
        try (FileReader fileReader = new FileReader(configFile)) {
            Gson gson = new GsonBuilder().create();
            GameRunnerConfig gameRunnerConfig = gson.fromJson(fileReader, GameRunnerConfig.class);

            if (gameRunnerConfig == null)
                throw new Exception("Failed to load gameRunnerConfig");

            gameRunnerConfig.matchId = UUID.randomUUID().toString();

            // Load tournament specific config here
            // Bot locations are dynamic, therefore, it's set in the PlayerBootstrapper
            if (gameRunnerConfig.isTournamentMode) {
                gameRunnerConfig.matchId = System.getenv(EnvironmentVariable.MATCH_ID.name());
            }

            if (gameRunnerConfig.gameName == null || gameRunnerConfig.gameName.isEmpty()) {
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                gameRunnerConfig.gameName = FileUtils.getAbsolutePath(gameRunnerConfig.roundStateOutputLocation) + "/" + timeStamp;
            }

            return gameRunnerConfig;
        }
    }
}
