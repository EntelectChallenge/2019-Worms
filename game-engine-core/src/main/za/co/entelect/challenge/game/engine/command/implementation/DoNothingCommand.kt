package za.co.entelect.challenge.game.engine.command.implementation

import za.co.entelect.challenge.game.engine.command.CommandValidation
import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm

/**
 * The player decides to do nothing
 */
class DoNothingCommand : WormsCommand {
    override val order: Int = 0

    override fun validate(gameMap: WormsMap, worm: Worm): CommandValidation {
        return CommandValidation.validMove(true, "Player chose to do nothing")
    }

    override fun execute(gameMap: WormsMap, worm: Worm) {
    }
}