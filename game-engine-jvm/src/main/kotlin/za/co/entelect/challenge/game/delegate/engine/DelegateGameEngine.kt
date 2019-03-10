package za.co.entelect.challenge.game.delegate.engine

import za.co.entelect.challenge.game.contracts.game.GameEngine
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.engine.WormsEngine

class DelegateGameEngine : GameEngine {

    private val wormsEngine = WormsEngine()

    override fun isGameComplete(map: GameMap): Boolean {
        return wormsEngine.isGameComplete()
    }

}
