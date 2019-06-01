package za.co.entelect.challenge.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import za.co.entelect.challenge.game.contracts.game.GameResult;

public interface TournamentApi {

    @POST("UpdateMatchStatus")
    Call<Void> updateMatchStatus(@Query("code") String code, @Body GameResult gameResult);
}
