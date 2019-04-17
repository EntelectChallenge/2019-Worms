package za.co.entelect.challenge.network;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;
import za.co.entelect.challenge.network.Dto.RunBotResponseDto;

public interface BotServices {

    @Multipart
    @POST("/instantiate_bot")
    Call<Void> instantiateBot(@Part MultipartBody.Part file);

    @Multipart
    @POST("/bot_command")
    Call<RunBotResponseDto> runBot(@Part MultipartBody.Part jsonState, @Part MultipartBody.Part textState, @Query("round_number") int roundNumber);

    @GET("/kill_bot")
    Call<Void> killBot();
}

//http://localhost:9002/run_bot?botDirectory=/path/to/bot/in/bot.json/&botFilename=reference-bot-1.0-SNAPSHOT-jar-with-dependencies.jar&language=java