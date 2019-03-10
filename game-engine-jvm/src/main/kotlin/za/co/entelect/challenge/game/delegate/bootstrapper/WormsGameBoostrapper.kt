package za.co.entelect.challenge.game.delegate.bootstrapper

import za.co.entelect.challenge.game.contracts.bootstrapper.GameEngineBootstrapper
import za.co.entelect.challenge.game.contracts.game.GameEngine
import za.co.entelect.challenge.game.contracts.game.GameMapGenerator
import za.co.entelect.challenge.game.contracts.game.GameRoundProcessor
import za.co.entelect.challenge.game.contracts.renderer.GameMapRenderer
import za.co.entelect.challenge.game.contracts.renderer.RendererType
import za.co.entelect.challenge.game.delegate.engine.DelegateGameEngine
import za.co.entelect.challenge.game.delegate.engine.DelegateMapGenerator
import za.co.entelect.challenge.game.delegate.engine.DelegateRoundProcessor
import za.co.entelect.challenge.game.delegate.factory.GameConfigFactory

class WormsGameBoostrapper : GameEngineBootstrapper {

    private var seed: Long = 0L
    private var configPath: String = "config.json"

    override fun setSeed(seed: Long) {
        this.seed = seed
    }

    override fun setConfigPath(configPath: String) {
        this.configPath = configPath
    }

    override fun getGameEngine(): GameEngine {
        return DelegateGameEngine()
    }

    override fun getMapGenerator(): GameMapGenerator {
        val config = GameConfigFactory.getConfig(configPath)
        return DelegateMapGenerator(config, seed)
    }

    override fun getRenderer(rendererType: RendererType): GameMapRenderer {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRoundProcessor(): GameRoundProcessor {
        return DelegateRoundProcessor()
    }
}