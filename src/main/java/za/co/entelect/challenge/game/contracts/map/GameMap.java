package za.co.entelect.challenge.game.contracts.map;

import za.co.entelect.challenge.game.contracts.game.GamePlayer;

public interface GameMap {

    int getCurrentRound();

    void setCurrentRound(int i);

    GamePlayer getWinningPlayer();
}
