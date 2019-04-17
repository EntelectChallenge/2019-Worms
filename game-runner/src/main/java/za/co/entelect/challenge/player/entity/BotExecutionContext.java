package za.co.entelect.challenge.player.entity;

import za.co.entelect.challenge.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class BotExecutionContext {

    public String jsonState;
    public String textState;
    public String consoleState;
    public String csvState;
    public String command;
    public String name;
    public int round;

    public void saveRoundStateData(String saveStateLocation) throws IOException {

        String mainRoundDirectory = String.format("%s/%s", saveStateLocation, FileUtils.getRoundDirectory(round));
        File fMain = new File(mainRoundDirectory);
        if (!fMain.exists()) {
            fMain.mkdirs();
        }

        File f = new File(String.format("%s/%s", mainRoundDirectory, name));
        if (!f.exists()) {
            f.mkdirs();
        }

        File fConsole = new File(String.format("%s/%s/%s", mainRoundDirectory, name, "Console"));
        if (!fConsole.exists()) {
            fConsole.mkdirs();
        }

        FileUtils.writeToFile(String.format("%s/%s/%s", mainRoundDirectory, name, "JsonMap.json"), jsonState);
        FileUtils.writeToFile(String.format("%s/%s/%s", mainRoundDirectory, name, "TextMap.txt"), textState);
        FileUtils.writeToFile(String.format("%s/%s/%s", mainRoundDirectory, name, "PlayerCommand.txt"), command);
        FileUtils.writeToFile(String.format("%s/%s/%s/%s", mainRoundDirectory, name, "Console", "Console.txt"), consoleState);
        FileUtils.appendToFile(String.format("%s/%s.csv", saveStateLocation, name), csvState);
    }
}
