package za.co.entelect.challenge.game.delegate.engine

import za.co.entelect.challenge.game.contracts.game.GameEngine
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.engine.WormsEngine
import za.co.entelect.challenge.game.engine.config.GameConfig

class DelegateGameEngine(config: GameConfig) : GameEngine {

    private val wormsEngine = WormsEngine(config)

    override fun isGameComplete(map: GameMap): Boolean {
        if (map !is DelegateMap) {
            throw IllegalArgumentException("Unknown Map Class ${map::class}")
        }

        return wormsEngine.isGameComplete(map.wormsMap)
    }

}
