package za.co.entelect.challenge.game.engine.processor

import mu.KotlinLogging
import za.co.entelect.challenge.game.engine.command.CommandExecutor
import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class WormsRoundProcessor(val config: GameConfig) {

    fun processRound(wormsMap: WormsMap, wormsCommands: Map<WormsPlayer, WormsCommand>): Boolean {
        logger.info { "Processing round: Round=${wormsMap.currentRound}" }

        val commands = wormsCommands.entries.sortedBy { it.value.order }
                .map { (player, command) -> CommandExecutor(player, wormsMap, command, config) }

        logger.info { "Executing Round commands: Round=${wormsMap.currentRound}, commands=$commands" }

        for (command in commands) {
            command.execute()
        }

        logger.info { "Updating player's active worms" }
        for (player in wormsMap.livingPlayers) {
            player.selectNextWorm()
        }

        logger.info { "Removing dead worms from the map" }
        wormsMap.removeDeadWorms()

        return true
    }

    /**
     * Returns all errors in the current round for the specific player
     */
    fun getErrorList(wormsMap: WormsMap, wormsPlayer: WormsPlayer): List<GameError> =
            wormsMap.currentRoundErrors.filter { it.player == wormsPlayer }

    /**
     * Returns all errors in the current round
     */
    fun getErrorList(wormsMap: WormsMap): List<GameError> = wormsMap.currentRoundErrors

    companion object {
        val logger = KotlinLogging.logger { }
    }
}
