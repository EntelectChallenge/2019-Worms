package za.co.entelect.challenge.game.engine

import za.co.entelect.challenge.game.contracts.command.RawCommand
import za.co.entelect.challenge.game.contracts.game.GamePlayer
import za.co.entelect.challenge.game.contracts.game.GameRoundProcessor
import za.co.entelect.challenge.game.contracts.map.GameMap

class DelegateRoundProcessor : GameRoundProcessor {
    override fun processRound(p0: GameMap?, p1: MutableMap<GamePlayer, RawCommand>?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getErrorList(): MutableList<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
