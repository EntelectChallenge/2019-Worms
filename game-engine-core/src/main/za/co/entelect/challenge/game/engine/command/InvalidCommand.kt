package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.entities.MoveValidation
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm

/**
 * Placeholder class for commands that could not be parsed so the command executor can update error lists
 * and player attributes
 */
class InvalidCommand(val reason: String) : WormsCommand {
    override val order: Int = 0

    override fun validate(gameMap: WormsMap, worm: Worm): MoveValidation {
        return MoveValidation.invalidMove(reason)
    }

    override fun execute(gameMap: WormsMap, worm: Worm) {
        throw NotImplementedError("Cannot execute invalid command")
    }
}