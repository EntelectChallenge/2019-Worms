package za.co.entelect.challenge.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import za.co.entelect.challenge.utils.FileUtils;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static GameRunnerConfig load(String configFile, String[] args) throws Exception {
        try (FileReader fileReader = new FileReader(configFile)) {
            Gson gson = new GsonBuilder().create();
            GameRunnerConfig gameRunnerConfig = gson.fromJson(fileReader, GameRunnerConfig.class);

            if (gameRunnerConfig == null)
                throw new Exception("Failed to load gameRunnerConfig");

            if (gameRunnerConfig.isTournamentMode) {

                if (args.length != 2)
                    throw new Exception("No bot locations specified for tournament");

                gameRunnerConfig.playerAConfig = args[0];
                gameRunnerConfig.playerBConfig = args[1];
            }

            if (gameRunnerConfig.gameName == null || gameRunnerConfig.gameName.isEmpty()) {
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                gameRunnerConfig.gameName = FileUtils.getAbsolutePath(gameRunnerConfig.roundStateOutputLocation) + "/" + timeStamp;
            }

            return gameRunnerConfig;
        }
    }
}
