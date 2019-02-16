package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.entities.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer

interface WormsCommand {

    /**
     * Tells this command to perform the required action within the command transaction provided
     *
     * @param gameMap The game map to make command calculations
     * @param player  The issuing player for this command
     */
    fun performCommand(gameMap: WormsMap, player: WormsPlayer)
}
