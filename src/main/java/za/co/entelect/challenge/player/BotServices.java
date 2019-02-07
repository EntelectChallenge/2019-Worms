package za.co.entelect.challenge.player;

import com.google.gson.JsonObject;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BotServices {

    @POST("/run_bot")
    public Call<JsonObject> runBot(@Query("botDirectory") String botDirectory, @Query("botFilename") String botFilename, @Query("language") String language);
}


//http://localhost:9002/run_bot?botDirectory=/path/to/bot/in/bot.json/&botFilename=reference-bot-1.0-SNAPSHOT-jar-with-dependencies.jar&language=java