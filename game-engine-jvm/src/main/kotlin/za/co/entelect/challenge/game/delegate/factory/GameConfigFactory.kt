package za.co.entelect.challenge.game.delegate.factory

import za.co.entelect.challenge.game.engine.entities.GameConfig

object GameConfigFactory {

    /**
     * TODO: Read from json file
     */
    fun getConfig(path: String): GameConfig {
        return GameConfig()
    }

}
