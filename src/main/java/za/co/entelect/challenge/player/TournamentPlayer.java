package za.co.entelect.challenge.player;

import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import za.co.entelect.challenge.botrunners.BotRunner;
import za.co.entelect.challenge.enums.BotLanguage;
import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.network.BotServices;
import za.co.entelect.challenge.network.Dto.RunBotResponseDto;
import za.co.entelect.challenge.player.entity.BotExecutionContext;
import za.co.entelect.challenge.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class TournamentPlayer extends BasePlayer {
    private static final String BOT_COMMAND = "command.txt";
    private static final String BOT_STATE = "state.json";
    private static final String TEXT_MAP = "textMap.txt";

    private final int apiPort;
    private final File botZip;

    private BotServices botServices;

    private static final Logger LOGGER = LogManager.getLogger(TournamentPlayer.class);

    public TournamentPlayer(String name, int apiPort, File botZip) throws Exception {
        super(name);
        this.apiPort = apiPort;
        this.botZip = botZip;

        String apiUrl = String.format("http://localhost:%d", apiPort);
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
        Response<Void> execute = instantiateBotCall.execute();
        if (!execute.isSuccessful()) {
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

            Response<RunBotResponseDto> execute = botServices.runBot(jsonPart, textPart).execute();
            return execute.body().getCommand();
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
    public void startGame(GameMap gameMap) {

    }
}
