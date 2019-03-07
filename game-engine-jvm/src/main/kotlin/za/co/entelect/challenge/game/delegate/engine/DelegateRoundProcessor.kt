package za.co.entelect.challenge.game.delegate.engine

import za.co.entelect.challenge.game.contracts.command.RawCommand
import za.co.entelect.challenge.game.contracts.game.GamePlayer
import za.co.entelect.challenge.game.contracts.game.GameRoundProcessor
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.delegate.factory.CommandParser
import za.co.entelect.challenge.game.delegate.player.DelegatePlayer
import za.co.entelect.challenge.game.engine.processor.WormsGameProcessor

class DelegateRoundProcessor : GameRoundProcessor {

    val wormsGameProcessor = WormsGameProcessor()

    override fun processRound(map: GameMap, commands: Map<GamePlayer, RawCommand>): Boolean {
        if (map !is DelegateMap) {
            throw IllegalArgumentException("Unknown Map Class")
        }

        val wormsCommands = commands.mapKeys { (key, _) ->
            (key as DelegatePlayer).wormsPlayer
        }.mapValues { (_, value) ->
            CommandParser.parseCommand(value)
        }

        return wormsGameProcessor.processRound(map.wormsMap, wormsCommands)
    }

    override fun getErrorList(): List<String> {
        return wormsGameProcessor.errorList
    }
}
