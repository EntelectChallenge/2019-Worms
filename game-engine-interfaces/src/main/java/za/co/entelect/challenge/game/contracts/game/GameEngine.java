package za.co.entelect.challenge.game.contracts.game;

import za.co.entelect.challenge.game.contracts.exceptions.TimeoutException;
import za.co.entelect.challenge.game.contracts.map.GameMap;

public interface GameEngine {

    boolean isGameComplete(GameMap gameMap) throws TimeoutException;
}
