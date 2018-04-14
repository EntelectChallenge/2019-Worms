package za.co.entelect.challenge.game.contracts.game;

import za.co.entelect.challenge.game.contracts.command.RawCommand;
import za.co.entelect.challenge.game.contracts.map.GameMap;

import java.util.ArrayList;
import java.util.Hashtable;

public interface GameRoundProcessor {

    boolean processRound(GameMap gameMap, Hashtable<GamePlayer, RawCommand> commandsToProcess);

    ArrayList<String> getErrorList();
}
