package za.co.entelect.challenge.game.engine

import za.co.entelect.challenge.game.contracts.command.RawCommand
import za.co.entelect.challenge.game.contracts.game.GamePlayer
import za.co.entelect.challenge.game.contracts.game.GameRoundProcessor
import za.co.entelect.challenge.game.contracts.map.GameMap

class WormsRoundProcessor : GameRoundProcessor {

    override fun processRound(gameMap: GameMap, commands: Map<GamePlayer, RawCommand>): Boolean {

        return true
    }

    override fun getErrorList(): List<String> {
        return emptyList()
    }
}
