package za.co.entelect.challenge.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Collections.singletonList;

public final class FileUtils {

    public static void writeToFile(String fileLocation, String stringToWrite) throws IOException {
        File stateDirectory = new File(fileLocation);
        stateDirectory.getParentFile().mkdirs();

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(stateDirectory));
        bufferedWriter.write(stringToWrite);
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public static void appendToFile(String fileLocation, String stringToWrite) throws IOException {
        Files.write(Paths.get(fileLocation), singletonList(stringToWrite), CREATE, WRITE, APPEND);
    }

    public static String getAbsolutePath(String path) {
        return new File(path).getAbsolutePath();
    }

    public static String getRoundDirectory(int roundNumber) {
        return String.format("Round %03d", roundNumber);
    }

    public static String getContainerPath(String path) {
        String file = new File(path).getPath();
        return String.format("/EntelectChallenge%s", file.substring(file.indexOf('/', file.indexOf('/', 1) + 1)));
    }
}
