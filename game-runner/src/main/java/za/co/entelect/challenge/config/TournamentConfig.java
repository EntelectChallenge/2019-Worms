package za.co.entelect.challenge.config;

import com.google.gson.annotations.SerializedName;

public class TournamentConfig {

    @SerializedName("tournament-id")
    public String tournamentId;

    @SerializedName("connection-string")
    public String connectionString;

    @SerializedName("bots-container")
    public String botsContainer;

    @SerializedName("match-logs-path")
    public String matchLogsPath;

    @SerializedName("game-engine-container")
    public String gameEngineContainer;

    @SerializedName("match-result-queue")
    public String matchResultQueue;

    @SerializedName("dead-match-queue")
    public String deadMatchQueue;

    @SerializedName("api-endpoint")
    public String apiEndpoint;

    @SerializedName("result-endpoint")
    public String resultEndpoint;

    @SerializedName("function-key")
    public String functionKey;
}
