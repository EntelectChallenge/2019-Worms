package za.co.entelect.challenge.game.engine.processor

import za.co.entelect.challenge.game.engine.command.CommandExecutor
import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class WormsRoundProcessor {

    fun processRound(wormsMap: WormsMap, wormsCommands: Map<WormsPlayer, WormsCommand>): Boolean {
        wormsMap.currentRound++

        val commands = wormsCommands.entries.sortedBy { it.value.order }
                .map { (player, command) -> CommandExecutor(player, wormsMap, command) }

        for (command in commands) {
            command.execute()
        }

        for (player in wormsMap.livingPlayers) {
            player.selectNextWorm()
        }

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
