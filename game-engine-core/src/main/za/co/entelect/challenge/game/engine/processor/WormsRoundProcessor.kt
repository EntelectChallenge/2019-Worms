package za.co.entelect.challenge.game.engine.processor

import za.co.entelect.challenge.game.engine.command.CommandExecutor
import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class WormsRoundProcessor(val config: GameConfig) {

    fun processRound(wormsMap: WormsMap, wormsCommands: Map<WormsPlayer, WormsCommand>): Boolean {
        wormsMap.startRound()

        val commands = wormsCommands.entries.sortedBy { it.value.order }
                .map { (player, command) -> CommandExecutor(player, wormsMap, command, config) }

        for (command in commands) {
            command.execute()
        }

        for (player in wormsMap.livingPlayers) {
            player.selectNextWorm()
        }

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
}
