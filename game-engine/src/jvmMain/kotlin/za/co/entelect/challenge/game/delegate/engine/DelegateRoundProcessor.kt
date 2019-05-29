package za.co.entelect.challenge.game.delegate.engine

import za.co.entelect.challenge.game.contracts.command.RawCommand
import za.co.entelect.challenge.game.contracts.game.GamePlayer
import za.co.entelect.challenge.game.contracts.game.GameRoundProcessor
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.delegate.player.DelegatePlayer
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.factory.CommandParser
import za.co.entelect.challenge.game.engine.processor.WormsRoundProcessor
import kotlin.random.Random

class DelegateRoundProcessor(random: Random, config: GameConfig) : GameRoundProcessor {

    private val wormsRoundProcessor = WormsRoundProcessor(config)
    private val commandParser = CommandParser(random, config)

    override fun processRound(map: GameMap, commands: Map<GamePlayer, RawCommand>): Boolean {
        if (map !is DelegateMap) {
            throw IllegalArgumentException("Unknown Map Class")
        }

        val wormsCommands = commands.mapKeys { (key, _) ->
            (key as DelegatePlayer).wormsPlayer
        }.mapValues { (_, value) ->
            commandParser.parseCommand(value.command)
        }

        return wormsRoundProcessor.processRound(map.wormsMap, wormsCommands.mapValues { listOf(it.value) })
    }

    override fun getErrorList(map: GameMap): List<String> {
        if (map !is DelegateMap) {
            throw IllegalArgumentException("Unknown Map Class")
        }

        return wormsRoundProcessor.getErrorList(map.wormsMap).map { it.toString() }
    }

    override fun getErrorList(map: GameMap, player: GamePlayer): List<String> {
        if (map !is DelegateMap) {
            throw IllegalArgumentException("Unknown Map Class")
        }

        if (player !is DelegatePlayer) {
            throw IllegalArgumentException("Unknown Player Class")
        }

        return wormsRoundProcessor.getErrorList(map.wormsMap, player.wormsPlayer).map { it.toString() }
    }
}
