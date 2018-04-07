package za.co.entelect.challenge.game.contracts.game;

import za.co.entelect.challenge.game.contracts.command.Command;
import za.co.entelect.challenge.game.contracts.map.GameMap;

import java.util.Hashtable;

public interface GameRoundProcessor {

    boolean processCommands(GameMap gameMap, Hashtable<GamePlayer, Command> commandsToProcess);
}
