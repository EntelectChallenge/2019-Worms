package za.co.entelect.challenge.game.contracts.command;

import za.co.entelect.challenge.game.contracts.exceptions.InvalidCommandException;
import za.co.entelect.challenge.game.contracts.game.GamePlayer;
import za.co.entelect.challenge.game.contracts.map.GameMap;

public interface Command {

    /**
     * Tells this command to perform the required action within the command transaction provided
     *
     * @param gameMap The game map to make command calculations
     * @param player  The issuing player for this command
     */
    void performCommand(GameMap gameMap, GamePlayer player) throws InvalidCommandException;
}
