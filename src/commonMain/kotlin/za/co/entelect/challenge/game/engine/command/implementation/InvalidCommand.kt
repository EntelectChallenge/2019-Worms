package za.co.entelect.challenge.game.engine.command.implementation

import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.feedback.CommandFeedback
import za.co.entelect.challenge.game.engine.command.feedback.CommandValidation
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm

/**
 * Placeholder class for commands that could not be parsed so the command executor can update error lists
 * and player attributes
 */
class InvalidCommand(val reason: String) : WormsCommand {
    override val order: Int = 0

    override fun validate(gameMap: WormsMap, worm: Worm): CommandValidation {
        return CommandValidation.invalidMove(reason)
    }

    override fun execute(gameMap: WormsMap, worm: Worm): CommandFeedback {
        throw NotImplementedError("Cannot execute invalid command")
    }
}