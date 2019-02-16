package za.co.entelect.challenge.game.delegate.engine

import za.co.entelect.challenge.game.delegate.factory.GameConfigFactory
import za.co.entelect.challenge.game.contracts.game.GameEngine
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.engine.WormsEngine

class DelegateGameEngine : GameEngine {

    private val config = GameConfigFactory.getConfig()
    private val wormsEngine = WormsEngine(config)

    override fun isGameComplete(map: GameMap): Boolean {
        return wormsEngine.isGameComplete()
    }

}
