package za.co.entelect.challenge.player.entity;

import za.co.entelect.challenge.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class BotExecutionState {

    public String jsonState;
    public String textState;
    public String consoleState;
    public String command;
    public String name;
    public int round;

    public void saveRoundStateData(String saveStateLocation) throws IOException {

        String mainDirectory = String.format("%s/%s", saveStateLocation, FileUtils.getRoundDirectory(round));
        File fMain = new File(mainDirectory);
        if (!fMain.exists()) {
            fMain.mkdirs();
        }

        File f = new File(String.format("%s/%s", mainDirectory, name));
        if (!f.exists()) {
            f.mkdirs();
        }

        File fConsole = new File(String.format("%s/%s/%s", mainDirectory, name, "Console"));
        if (!fConsole.exists()) {
            fConsole.mkdirs();
        }

        FileUtils.writeToFile(String.format("%s/%s/%s", mainDirectory, name, "JsonMap.json"), jsonState);
        FileUtils.writeToFile(String.format("%s/%s/%s", mainDirectory, name, "TextMap.txt"), textState);
        FileUtils.writeToFile(String.format("%s/%s/%s", mainDirectory, name, "PlayerCommand.txt"), command);
        FileUtils.writeToFile(String.format("%s/%s/%s/%s", mainDirectory, name, "Console", "Console.txt"), consoleState);
    }
}
