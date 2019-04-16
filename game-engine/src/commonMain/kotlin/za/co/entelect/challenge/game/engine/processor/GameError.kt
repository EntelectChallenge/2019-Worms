package za.co.entelect.challenge.game.engine.processor

import za.co.entelect.challenge.game.engine.player.Worm
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class GameError(val message: String, val player: WormsPlayer, val worm: Worm, val round: Int) {

    override fun toString(): String {
        return "GameError - Player ${player.id}, worm ${worm.id}, round $round: $message"
    }

}