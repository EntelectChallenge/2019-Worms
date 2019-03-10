package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.entities.MoveValidation
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm

interface WormsCommand {

    val order: Int

    /**
     * Checks if the command is valid without changing anything.
     */
    fun validate(gameMap: WormsMap, worm: Worm): MoveValidation

    /**
     * Tells this command to perform the required action within the command transaction provided
     *
     * @param gameMap The game map to make command calculations
     * @param worm  The issuing occupier for this command
     */
    fun execute(gameMap: WormsMap, worm: Worm)
}
