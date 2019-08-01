package za.co.entelect.challenge.game.contracts.map;

import za.co.entelect.challenge.game.contracts.common.RefereeMessage;
import za.co.entelect.challenge.game.contracts.game.GamePlayer;

public interface GameMap {

    int getCurrentRound();

    /**
     * The game runner is responsible for setting the round number at the beginning of every round
     */
    void setCurrentRound(int i);

    GamePlayer getWinningPlayer();

    RefereeMessage getRefereeIssues();
}
