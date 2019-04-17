package za.co.entelect.challenge.player;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import za.co.entelect.challenge.config.GameRunnerConfig;
import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.network.BotServices;
import za.co.entelect.challenge.network.Dto.RunBotResponseDto;
import za.co.entelect.challenge.network.RetryableCall;
import za.co.entelect.challenge.player.entity.BasePlayer;
import za.co.entelect.challenge.player.entity.BotExecutionContext;

import java.io.File;
import java.io.IOException;

public class TournamentPlayer extends BasePlayer {

    private int apiPort;
    private File botZip;
    private BotServices botServices;
    private int maxRequestRetries;
    private int retryTimeout;

    private static final Logger LOGGER = LogManager.getLogger(TournamentPlayer.class);

    public TournamentPlayer(GameRunnerConfig gameRunnerConfig, String name, int apiPort, File botZip) throws Exception {
        super(name);
        this.apiPort = apiPort;
        this.botZip = botZip;
        this.maxRequestRetries = gameRunnerConfig.maxRequestRetries;
        this.retryTimeout = gameRunnerConfig.requestTimeout;

        String apiUrl = String.format("%s:%d", gameRunnerConfig.tournamentConfig.apiEndpoint, apiPort);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        botServices = retrofit.create(BotServices.class);
        instantiateBot();
    }

    private void instantiateBot() throws Exception {

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/zip"), botZip);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", botZip.getName(), requestBody);

        Call<Void> instantiateBotCall = botServices.instantiateBot(fileToUpload);

        //Decorate the standard call with retry capability
        instantiateBotCall = new RetryableCall<>(instantiateBotCall, maxRequestRetries, retryTimeout);

        Response<Void> response = instantiateBotCall.execute();
        if (!response.isSuccessful()) {
            String errorMessage = String.format("Failed to instantiate bot: %s on api port %d", getName(), apiPort);

            LOGGER.error(errorMessage);
            throw new Exception(errorMessage);
        }

        LOGGER.info(String.format("Successfully instantiated bot: %s on api port %d", getName(), apiPort));
    }

    @Override
    public String getCommand(BotExecutionContext botExecutionContext) {

        try {
            MultipartBody.Part jsonPart = createPart("json", botExecutionContext.jsonState);
            MultipartBody.Part textPart = createPart("text", botExecutionContext.textState);

            Response<RunBotResponseDto> execute = botServices.runBot(jsonPart, textPart, botExecutionContext.round).execute();
            return execute.body().getCommand().trim();
        } catch (IOException e) {
            LOGGER.error("Failed to get bot command", e);
        }

        return NO_COMMAND;
    }

    private MultipartBody.Part createPart(String partName, String content) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), content);
        return MultipartBody.Part.createFormData(partName, partName, requestBody);
    }

    @Override
    public void gameEnded(GameMap gameMap) {
        super.gameEnded(gameMap);

        LOGGER.info(String.format("Signaling bot runner to shutdown - Player id: %s", getPlayerId()));
        try {
            botServices.killBot().execute();
        }
        catch (IOException e) {
            LOGGER.warn(String.format("Request to runner failed due to the shutdown signal - Player id: %s", getPlayerId()));
        }
    }
}
