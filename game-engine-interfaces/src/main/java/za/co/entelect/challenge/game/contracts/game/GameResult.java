package za.co.entelect.challenge.game.contracts.game;

import java.util.ArrayList;
import java.util.List;

public class GameResult {

    public String matchId = "";
    public String tournamentId;
    public boolean isComplete = false;
    public boolean isSuccessful = false;
    public boolean verificationRequired = true;
    public String winner;
    public long playerOnePoints = 0;
    public long playerTwoPoints = 0;
    public String playerAId;
    public String playerBId;
    public long roundsPlayed = 0;
    public String playerAEntryId;
    public List<PlayerResult> playerResults;

    public GameResult() {
        playerResults = new ArrayList<>();
    }

    public void addPlayerResult(String playerId, long score) {
        playerResults.add(new PlayerResult(playerId, score));
    }

    private class PlayerResult {
        String playerId;
        long score;

        public PlayerResult(String playerId, long score) {
            this.playerId = playerId;
            this.score = score;
        }
    }
}
