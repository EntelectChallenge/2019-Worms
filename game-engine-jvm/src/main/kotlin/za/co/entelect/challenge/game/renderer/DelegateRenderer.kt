package za.co.entelect.challenge.game.renderer

import za.co.entelect.challenge.game.contracts.game.GamePlayer
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.contracts.renderer.GameMapRenderer
import za.co.entelect.challenge.game.contracts.renderer.RendererType
import za.co.entelect.challenge.game.delegate.engine.DelegateMap
import za.co.entelect.challenge.game.delegate.player.DelegatePlayer
import za.co.entelect.challenge.game.engine.config.GameConfig

class DelegateRenderer(config: GameConfig, rendererType: RendererType) : GameMapRenderer {

    private val renderer = WormsRenderer(config, rendererType)

    override fun commandPrompt(gamePlayer: GamePlayer?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun render(gameMap: GameMap?, player: GamePlayer?): String {
        if (gameMap !is DelegateMap) {
            throw IllegalArgumentException("Unknown Map Class")
        }

        if (player !is DelegatePlayer) {
            throw IllegalArgumentException("Unknown Map Class")
        }

        return renderer.render(gameMap.wormsMap, player.wormsPlayer)
    }

}
