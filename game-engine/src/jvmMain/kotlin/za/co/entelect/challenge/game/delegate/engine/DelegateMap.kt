package za.co.entelect.challenge.game.delegate.engine

import za.co.entelect.challenge.game.contracts.game.GamePlayer
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.delegate.player.DelegatePlayer
import za.co.entelect.challenge.game.engine.map.WormsMap

class DelegateMap(val wormsMap: WormsMap) : GameMap {

    override fun getWinningPlayer(): GamePlayer? {
        return wormsMap.winningPlayer?.let { DelegatePlayer(it) }
    }

    override fun getCurrentRound(): Int {
        return wormsMap.currentRound
    }

    override fun setCurrentRound(round: Int) {
        wormsMap.currentRound = round
    }
}
