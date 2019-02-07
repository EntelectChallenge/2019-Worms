package za.co.entelect.challenge.bootstrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import za.co.entelect.challenge.utils.FileUtils;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Config {

    @SerializedName("game-name")
    public String gameName;

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

    public static Config load(String configFile, String[] args) throws Exception {
        try (FileReader fileReader = new FileReader(configFile)) {
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

            if (config.gameName == null || config.gameName.isEmpty()) {
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                config.gameName = FileUtils.getAbsolutePath(config.roundStateOutputLocation) + "/" + timeStamp;
            }

            return config;
        }
    }
}
