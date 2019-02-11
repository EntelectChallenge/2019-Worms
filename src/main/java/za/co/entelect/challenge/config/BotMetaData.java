package za.co.entelect.challenge.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import za.co.entelect.challenge.enums.BotLanguage;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class BotMetaData {

    @SerializedName("author")
    private String author;

    @SerializedName("email")
    private String email;

    @SerializedName("nickName")
    private String nickName;

    @SerializedName("botLanguage")
    private BotLanguage botLanguage;

    @SerializedName("botLocation")
    private String botLocation;

    @SerializedName("botFileName")
    private String botFileName;

    @SerializedName("arguments")
    private BotArguments arguments;

    public BotMetaData(BotLanguage language, String botLocation, String botFileName) {
        this.botLanguage = language;
        this.botLocation = botLocation;
        this.botFileName = botFileName;
    }

    public String getAuthor() {
        return author;
    }

    public String getEmail() {
        return email;
    }

    public String getNickName() {
        return nickName;
    }

    public String getBotLocation() {
        return this.botLocation;
    }

    public void setRelativeBotLocation(String relativeLocation) {
        this.botLocation = relativeLocation + this.botLocation;
    }

    public String getBotFileName() {
        return this.botFileName;
    }

    public BotLanguage getBotLanguage() {
        return this.botLanguage;
    }

    public String getBotDirectory() {
        return Paths.get(getBotLocation()).toAbsolutePath().normalize().toString();
    }

    public BotArguments getArguments() {
        return this.arguments;
    }

    public static BotMetaData load(String botLocation) throws Exception {

        Optional<Path> botMetaPath = Files.walk(Paths.get(botLocation))
                .filter(path -> path.endsWith("bot.json"))
                .findFirst();

        if (!botMetaPath.isPresent()) {
            throw new Exception("Failed to find bot meta data from location: " + botLocation);
        }

        try (FileReader fileReader = new FileReader(botMetaPath.get().toFile())) {

            Gson gson = new GsonBuilder().create();

            BotMetaData botMeta = gson.fromJson(fileReader, BotMetaData.class);
            botMeta.setRelativeBotLocation(botLocation);

            return botMeta;
        }
    }
}
