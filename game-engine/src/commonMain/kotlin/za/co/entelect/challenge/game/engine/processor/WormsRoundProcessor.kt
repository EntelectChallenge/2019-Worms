package za.co.entelect.challenge.game.engine.processor

import mu.KotlinLogging
import za.co.entelect.challenge.game.engine.command.CommandExecutor
import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.implementation.DoNothingCommand
import za.co.entelect.challenge.game.engine.command.implementation.SelectCommand
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class WormsRoundProcessor(val config: GameConfig) {

    fun processRound(wormsMap: WormsMap, wormsCommands: Map<WormsPlayer, List<WormsCommand>>): Boolean {
        val mutableCommandsMap = wormsCommands.toMutableMap()

        logger.info { "Processing round: Round=${wormsMap.currentRound}" }

        executeSelectCommands(mutableCommandsMap, wormsMap)
        executeOtherCommands(mutableCommandsMap, wormsMap)

        logger.info { "Updating player's active worms" }
        for (player in wormsMap.livingPlayers) {
            player.selectNextWorm()
        }

        logger.info { "Applying powerups" }
        wormsMap.applyHealthPacks()

        logger.info { "Adding kill scores for dead worms" }
        wormsMap.setScoresForKilledWorms(config)

        logger.info { "Removing dead worms from the map" }
        wormsMap.removeDeadWorms()

        logger.info { "Checking for referee issues" }
        wormsMap.detectRefereeIssues()

        return true
    }

    private fun executeSelectCommands(commands: Map<WormsPlayer, List<WormsCommand>>, wormsMap: WormsMap) {
        val selectCommands = commands.mapValues {
            it.value.lastOrNull { command -> command is SelectCommand }
        }.filterNullValues()

        val commandExecutors = buildCommandExecutors(selectCommands, wormsMap)
        logger.info { "Executing Round Select commands: Round=${wormsMap.currentRound}, commands=$commandExecutors" }

        commandExecutors.forEach { it.execute() }
    }

    private fun executeOtherCommands(commands: Map<WormsPlayer, List<WormsCommand>>, wormsMap: WormsMap) {
        val otherCommands = commands.mapValues {
            it.value.filter { command -> command !is SelectCommand }
        }.withMissingCommands(wormsMap.players)

        val commandExecutors = buildCommandExecutors(otherCommands, wormsMap)
        logger.info { "Executing Round commands: Round=${wormsMap.currentRound}, commands=$commandExecutors" }

        commandExecutors.forEach { it.execute() }
    }

    private fun Map<WormsPlayer, List<WormsCommand>>.withMissingCommands(players: List<WormsPlayer>): Map<WormsPlayer, WormsCommand> {
        val doNothingCommand = DoNothingCommand(config, "No command received for player")

        return players.groupBy({ it }, { player -> get(player).lastOrDefault(doNothingCommand) })
                .mapValues { (_, commands) -> commands.last() }
    }

    private fun buildCommandExecutors(commands: Map<WormsPlayer, WormsCommand>, wormsMap: WormsMap): List<CommandExecutor> {
        return commands.entries.sortedBy { it.value.order }
                .map { (player, command) -> CommandExecutor(player, wormsMap, command, config) }
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
        private val logger = KotlinLogging.logger { }
    }
}

@Suppress("UNCHECKED_CAST")
private fun <K, V> Map<K, V?>.filterNullValues(): Map<K, V> = filterValues { value -> value != null } as Map<K, V>

private fun <T> List<T>?.lastOrDefault(default: T) = this?.lastOrNull() ?: default
