package za.co.entelect.challenge.game.contracts.game;

import za.co.entelect.challenge.game.contracts.common.RefereeMessage;
import za.co.entelect.challenge.game.contracts.map.GameMap;

public interface GameReferee {

    RefereeMessage isMatchValid(GameMap gameMap);
}
