package za.co.entelect.challenge;

import com.google.gson.Gson;
import za.co.entelect.challenge.entities.GameState;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private static final String COMMAND_FILE_NAME = "command.txt";
    private static final String STATE_FILE_NAME = "state.json";

    /**
     * Read the current state, feed it to the bot, get the output and write it to the command.
     * @param args the args
     **/
    public static void main(String[] args) {
        String state = null;
        try {
            state = new String(Files.readAllBytes(Paths.get(STATE_FILE_NAME)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        GameState gameState = gson.fromJson(state, GameState.class);

        Bot bot = new Bot(gameState);
        String command = bot.run();

        writeBotResponseToFile(command);
    }

    /**
     * Write bot response to file
     * @param command the command
     **/
    private static void writeBotResponseToFile(String command) {
        try {
            Files.write(Paths.get(COMMAND_FILE_NAME), command.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
