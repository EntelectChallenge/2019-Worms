package za.co.entelect.challenge.game.delegate.renderer

import za.co.entelect.challenge.game.contracts.game.GamePlayer
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.contracts.renderer.GameMapRenderer
import za.co.entelect.challenge.game.contracts.renderer.RendererType
import za.co.entelect.challenge.game.delegate.engine.DelegateMap
import za.co.entelect.challenge.game.delegate.player.DelegatePlayer
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.renderer.WormsRendererConsole
import za.co.entelect.challenge.game.engine.renderer.WormsRendererCsv
import za.co.entelect.challenge.game.engine.renderer.WormsRendererJson
import za.co.entelect.challenge.game.engine.renderer.WormsRendererText

class DelegateRenderer(config: GameConfig, rendererType: RendererType) : GameMapRenderer {

    private val renderer = when (rendererType) {
        RendererType.JSON -> WormsRendererJson(config)
        RendererType.TEXT -> WormsRendererText(config)
        RendererType.CONSOLE -> WormsRendererConsole(config)
        RendererType.CSV -> WormsRendererCsv(config)
    }

    override fun commandPrompt(gamePlayer: GamePlayer?): String {
        if (gamePlayer !is DelegatePlayer) {
            throw IllegalArgumentException("Unknown Player Class")
        }
        return renderer.commandPrompt(gamePlayer.wormsPlayer)
    }

    override fun render(gameMap: GameMap?, player: GamePlayer?): String {
        if (gameMap !is DelegateMap) {
            throw IllegalArgumentException("Unknown Map Class")
        }

        if (player !is DelegatePlayer) {
            throw IllegalArgumentException("Unknown Player Class")
        }

        return renderer.render(gameMap.wormsMap, player.wormsPlayer)
    }

}
