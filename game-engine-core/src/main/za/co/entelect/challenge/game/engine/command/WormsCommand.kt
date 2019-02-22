package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.entities.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm

interface WormsCommand {
    /**
     * Checks if the command is valid without changing anything.
     */
    fun isValid(gameMap: WormsMap, player: Worm): Boolean

    /**
     * Tells this command to perform the required action within the command transaction provided
     *
     * @param gameMap The game map to make command calculations
     * @param worm  The issuing occupier for this command
     * @throws InvalidCommandException when an invalid command is executed.
     */
    fun execute(gameMap: WormsMap, worm: Worm)
}
