package za.co.entelect.challenge.game.entities;

import za.co.entelect.challenge.game.contracts.game.GamePlayer
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.contracts.player.Player


public class DelegateMap(val players: List<Player>) : GameMap {


    override fun getWinningPlayer(): GamePlayer {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCurrentRound(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCurrentRound(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
