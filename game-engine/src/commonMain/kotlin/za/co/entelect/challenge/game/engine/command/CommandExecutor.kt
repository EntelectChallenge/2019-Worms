package za.co.entelect.challenge.game.engine.command

import mu.KotlinLogging
import za.co.entelect.challenge.game.engine.command.feedback.StandardCommandFeedback
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.processor.GameError

class CommandExecutor(private val player: WormsPlayer,
                      private val map: WormsMap,
                      private val command: WormsCommand,
                      private val config: GameConfig) {

    val worm = player.currentWorm
    // Moves are validated on command executor construction
    private val moveValidation = command.validate(map, worm)

    fun execute() {
        logger.info { "Executing command $worm Command($command) $moveValidation " }

        if (moveValidation.isNothing) {
            player.consecutiveDoNothingsCount++
        } else {
            player.consecutiveDoNothingsCount = 0
        }

        if (moveValidation.isValid) {
            val commandFeedback = command.execute(map, worm)

            logger.info { "Executed command $worm $commandFeedback" }

            player.commandScore += commandFeedback.score
            map.addFeedback(commandFeedback)

            if (!commandFeedback.success) {
                addErrorToMap(commandFeedback.message)
            }
        } else {
            map.addFeedback(StandardCommandFeedback("invalid", config.scores.invalidCommand, player.id, false))
            addErrorToMap(moveValidation.reason)
            player.commandScore += config.scores.invalidCommand
        }
    }

    private fun addErrorToMap(message: String) {
        map.addError(GameError(message, player, worm, map.currentRound))
    }

    override fun toString(): String {
        return "CommandExecutor(worm=$worm, command=\"$command\")"
    }

    companion object {
        private val logger = KotlinLogging.logger{}
    }

}
