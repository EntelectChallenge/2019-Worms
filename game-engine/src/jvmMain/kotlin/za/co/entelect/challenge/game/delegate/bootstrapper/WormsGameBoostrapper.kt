package za.co.entelect.challenge.game.delegate.bootstrapper

import za.co.entelect.challenge.game.contracts.bootstrapper.GameEngineBootstrapper
import za.co.entelect.challenge.game.contracts.game.GameEngine
import za.co.entelect.challenge.game.contracts.game.GameMapGenerator
import za.co.entelect.challenge.game.contracts.game.GameReferee
import za.co.entelect.challenge.game.contracts.game.GameRoundProcessor
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.contracts.renderer.GameMapRenderer
import za.co.entelect.challenge.game.contracts.renderer.RendererType
import za.co.entelect.challenge.game.delegate.engine.DelegateGameEngine
import za.co.entelect.challenge.game.delegate.engine.DelegateMapGenerator
import za.co.entelect.challenge.game.delegate.engine.DelegateRoundProcessor
import za.co.entelect.challenge.game.delegate.factory.GameConfigFactory
import za.co.entelect.challenge.game.delegate.referee.DelegateReferee
import za.co.entelect.challenge.game.delegate.renderer.DelegateRenderer
import kotlin.random.Random

class WormsGameBoostrapper : GameEngineBootstrapper {

    private var seed: Int = 0
    private var configPath: String = "default-config.json"

    override fun setSeed(seed: Int) {
        this.seed = seed
    }

    override fun setConfigPath(configPath: String) {
        this.configPath = configPath
    }

    override fun getGameEngine(): GameEngine {
        val config = GameConfigFactory.getConfig(configPath)
        return DelegateGameEngine(config)
    }

    override fun getMapGenerator(): GameMapGenerator {
        val config = GameConfigFactory.getConfig(configPath)
        return DelegateMapGenerator(config, seed)
    }

    override fun getRenderer(rendererType: RendererType): GameMapRenderer {
        val config = GameConfigFactory.getConfig(configPath)
        return DelegateRenderer(config, rendererType)
    }

    override fun getRoundProcessor(): GameRoundProcessor {
        val config = GameConfigFactory.getConfig(configPath)
        return DelegateRoundProcessor(Random(seed), config)
    }

    override fun getReferee(map: GameMap): GameReferee {
        return DelegateReferee(map)
    }
}
