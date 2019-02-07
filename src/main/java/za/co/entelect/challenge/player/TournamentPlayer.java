package za.co.entelect.challenge.player;

import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import za.co.entelect.challenge.botrunners.BotRunner;
import za.co.entelect.challenge.core.renderers.TowerDefenseConsoleMapRenderer;
import za.co.entelect.challenge.core.renderers.TowerDefenseJsonGameMapRenderer;
import za.co.entelect.challenge.core.renderers.TowerDefenseTextMapRenderer;
import za.co.entelect.challenge.enums.BotLanguage;
import za.co.entelect.challenge.game.contracts.command.RawCommand;
import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.game.contracts.player.Player;
import za.co.entelect.challenge.game.contracts.renderer.GameMapRenderer;
import za.co.entelect.challenge.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class TournamentPlayer extends Player {
    private static final String BOT_COMMAND = "command.txt";
    private static final String BOT_STATE = "state.json";
    private static final String TEXT_MAP = "textMap.txt";

    private GameMapRenderer jsonRenderer;
    private GameMapRenderer textRenderer;
    private GameMapRenderer consoleRenderer;
    private BotRunner botRunner;
    private BotLanguage botLanguage;
    private BotServices botServices;
    private String saveStateLocation;

    private static final Logger log = LogManager.getLogger(BotPlayer.class);

    public TournamentPlayer(String name, BotRunner botRunner, BotLanguage botLanguage, String saveStateLocation) {
        super(name);

        this.botRunner = botRunner;
        this.botLanguage = botLanguage;
        this.saveStateLocation = saveStateLocation;
        jsonRenderer = new TowerDefenseJsonGameMapRenderer();
        textRenderer = new TowerDefenseTextMapRenderer();
        consoleRenderer = new TowerDefenseConsoleMapRenderer();

        String dockerUrl = String.format("http://localhost:%d", botRunner.getDockerPort());

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(dockerUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        botServices = retrofit.create(BotServices.class);
    }

    @Override
    public void startGame(GameMap gameMap) {
        newRoundStarted(gameMap);
    }

    @Override
    public void newRoundStarted(GameMap gameMap) {

        String botInput = "No Command";
        try {
            String playerSpecificJsonState = jsonRenderer.render(gameMap, getGamePlayer());
            String playerSpecificTextState = textRenderer.render(gameMap, getGamePlayer());
            String playerSpecificConsoleState = consoleRenderer.render(gameMap, getGamePlayer());

            writeStateFiles(playerSpecificJsonState, playerSpecificTextState);

            Call<JsonObject> call = botServices.runBot(FileUtils.getContainerPath(botRunner.getBotDirectory()), botRunner.getBotFileName(), botLanguage.toString());
            Response<JsonObject> execution = call.execute();

            if (!execution.isSuccessful()) {
                throw new RuntimeException(String.format("Unable to run bot in container. Language: %s Port: %d Response code: %d Response body: %s", botLanguage.toString(), botRunner.getDockerPort(), execution.code(), execution.errorBody().string()));
            }

            String consoleOutput = execution.body().get("message").toString();

            File botCommandFile = new File(String.format("%s/%s", botRunner.getBotDirectory(), BOT_COMMAND));
            try (Scanner scanner = new Scanner(botCommandFile)) {
                if (scanner.hasNext()) {
                    botInput = scanner.nextLine();
                }

            } catch (FileNotFoundException e) {
                log.info(String.format("File %s not found", botRunner.getBotDirectory() + "/" + BOT_COMMAND));
                botInput = "Exception";
            }

            BotPlayer.writeRoundStateData(playerSpecificJsonState,
                    playerSpecificTextState,
                    playerSpecificConsoleState,
                    botInput,
                    gameMap.getCurrentRound(),
                    consoleOutput,
                    getName(),
                    saveStateLocation);

        } catch (RuntimeException | IOException e) {
            log.warn(e);
            e.printStackTrace();
            botInput = "Exception";
        }

        RawCommand rawCommand = new RawCommand(botInput);
        publishCommand(rawCommand);
    }

    @Override
    public void gameEnded(GameMap gameMap) {
        String playerSpecificJsonState = jsonRenderer.render(gameMap, getGamePlayer());
        String playerSpecificTextState = textRenderer.render(gameMap, getGamePlayer());
        String playerSpecificConsoleState = consoleRenderer.render(gameMap, getGamePlayer());

        try {
            BotPlayer.writeRoundStateData(
                    playerSpecificJsonState,
                    playerSpecificTextState,
                    playerSpecificConsoleState,
                    "",
                    gameMap.getCurrentRound(),
                    "",
                    getName(),
                    saveStateLocation);
            writeStateFiles(playerSpecificJsonState, playerSpecificTextState);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeStateFiles(String state, String textState) throws IOException {
        File existingCommandFile = new File(String.format("%s/%s", botRunner.getBotDirectory(), BOT_COMMAND));

        if (existingCommandFile.exists()) {
            existingCommandFile.delete();
        }

        FileUtils.writeToFile(String.format("%s/%s", botRunner.getBotDirectory(), BOT_STATE), state);
        FileUtils.writeToFile(String.format("%s/%s", botRunner.getBotDirectory(), TEXT_MAP), textState);
    }

}
