package za.co.entelect.challenge.game.delegate.player

import za.co.entelect.challenge.game.contracts.game.GamePlayer
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class DelegatePlayer(val wormsPlayer: WormsPlayer) : GamePlayer {

    override fun getHealth(): Int = wormsPlayer.health
    override fun getScore(): Int = wormsPlayer.score
}