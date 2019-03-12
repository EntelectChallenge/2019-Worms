package za.co.entelect.challenge.game.renderer

import za.co.entelect.challenge.game.contracts.game.GamePlayer
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.contracts.renderer.GameMapRenderer
import za.co.entelect.challenge.game.contracts.renderer.RendererType
import za.co.entelect.challenge.game.engine.config.GameConfig

class DelegateRenderer(private val config: GameConfig, rendererType: RendererType) : GameMapRenderer {

    private val renderer = WormsRenderer(config, rendererType)

    override fun commandPrompt(gamePlayer: GamePlayer?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun render(gameMap: GameMap?, player: GamePlayer?): String {
        TODO("no")
//        return renderer.render(gameMap, player)
    }


}
