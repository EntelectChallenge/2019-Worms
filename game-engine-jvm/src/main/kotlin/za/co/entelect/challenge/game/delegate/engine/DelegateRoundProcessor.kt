package za.co.entelect.challenge.game.delegate.engine

import za.co.entelect.challenge.game.contracts.command.RawCommand
import za.co.entelect.challenge.game.contracts.game.GamePlayer
import za.co.entelect.challenge.game.contracts.game.GameRoundProcessor
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.delegate.player.DelegatePlayer
import za.co.entelect.challenge.game.engine.factory.CommandParser
import za.co.entelect.challenge.game.engine.processor.WormsGameProcessor
import kotlin.random.Random

class DelegateRoundProcessor(random: Random) : GameRoundProcessor {

    private val wormsGameProcessor = WormsGameProcessor()
    private val commandParser = CommandParser(random)

    override fun processRound(map: GameMap, commands: Map<GamePlayer, RawCommand>): Boolean {
        if (map !is DelegateMap) {
            throw IllegalArgumentException("Unknown Map Class")
        }

        val wormsCommands = commands.mapKeys { (key, _) ->
            (key as DelegatePlayer).wormsPlayer
        }.mapValues { (_, value) ->
            commandParser.parseCommand(value.command)
        }

        return wormsGameProcessor.processRound(map.wormsMap, wormsCommands)
    }

    override fun getErrorList(): List<String> {
        return wormsGameProcessor.errorList
    }
}
