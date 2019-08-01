package za.co.entelect.challenge.game.engine

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.GameMap

class WormsEngine(private val config: GameConfig) {
    fun isGameComplete(wormsMap: GameMap): Boolean {
        return wormsMap.currentRound >= config.maxRounds ||
                wormsMap.livingPlayers.size <= 1
    }
}
