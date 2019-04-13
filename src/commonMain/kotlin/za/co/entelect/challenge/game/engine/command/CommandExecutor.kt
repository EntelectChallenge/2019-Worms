package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.command.feedback.CommandFeedback
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.processor.GameError

class CommandExecutor(val player: WormsPlayer,
                      val map: WormsMap,
                      val command: WormsCommand,
                      val config: GameConfig) {

    val worm = player.currentWorm
    // Moves are validated on command executor construction
    private val moveValidation = command.validate(map, worm)

    fun execute() {
        if (moveValidation.isNothing) {
            player.consecutiveDoNothingsCount++
        } else {
            player.consecutiveDoNothingsCount = 0
        }

        if (moveValidation.isValid) {
            val commandFeedback = command.execute(map, worm)
            player.commandScore += commandFeedback.score
            map.currentRoundFeedback.add(commandFeedback)

            if (!commandFeedback.success) {
                addErrorToMap(commandFeedback.message)
            }
        } else {
            map.currentRoundFeedback.add(CommandFeedback("invalid", config.scores.invalidCommand, player.id, false))
            addErrorToMap(moveValidation.reason)
            player.commandScore += config.scores.invalidCommand
        }
    }

    private fun addErrorToMap(message: String) {
        map.addError(GameError(message, player, worm, map.currentRound))
    }

}
