package za.co.entelect.challenge.game.engine.command

import mu.KotlinLogging
import za.co.entelect.challenge.game.engine.command.feedback.CommandFeedback
import za.co.entelect.challenge.game.engine.command.feedback.StandardCommandFeedback
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.processor.GameError
import za.co.entelect.challenge.game.engine.renderer.printables.VisualizerEvent

class CommandExecutor(private val player: WormsPlayer,
                      private val map: WormsMap,
                      private val command: WormsCommand,
                      private val config: GameConfig) {

    val worm = player.currentWorm
    // Moves are validated on command executor construction
    private val moveValidation = command.validate(map, worm)
    private val freezeDuration = config.technologistWorms.snowballs!!.freezeDuration

    fun execute() {
        logger.info { "Executing command $worm Command($command) $moveValidation " }

        when {
            moveValidation.isNothing -> player.consecutiveDoNothingsCount++
            else -> player.consecutiveDoNothingsCount = 0
        }

        if (!command.ignoresBeingFrozen() && worm.roundsUntilUnfrozen != freezeDuration && worm.roundsUntilUnfrozen > 0) {
            logger.info { "Tried to execute command $command, but $worm is still frozen for ${worm.roundsUntilUnfrozen} round" }
            map.addFeedback(StandardCommandFeedback(command.toString(), 0, player.id, false, "Frozen worms cannot follow your commands",
                    VisualizerEvent(CommandStrings.NOTHING.string, "frozen", worm, null, null, null)))
        } else if (moveValidation.isValid) {
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
        private val logger = KotlinLogging.logger {}
    }

}
