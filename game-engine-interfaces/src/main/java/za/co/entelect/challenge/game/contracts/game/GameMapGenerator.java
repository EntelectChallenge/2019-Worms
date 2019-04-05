package za.co.entelect.challenge.game.contracts.game;

import za.co.entelect.challenge.game.contracts.map.GameMap;
import za.co.entelect.challenge.game.contracts.player.Player;

import java.util.List;

public interface GameMapGenerator {

    GameMap generateGameMap(List<Player> players);
}
