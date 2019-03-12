package za.co.entelect.challenge.game.renderer

import com.google.gson.Gson
import za.co.entelect.challenge.game.contracts.game.GamePlayer
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.contracts.renderer.GameMapRenderer
import za.co.entelect.challenge.game.contracts.renderer.RendererType
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import java.lang.Exception


class WormsRenderer(private val config: GameConfig, private val rendererType: RendererType) {

    private val gson = Gson()

    fun commandPrompt(gamePlayer: GamePlayer?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun render(wormsMap: WormsMap, player: WormsPlayer): String {
        return when (rendererType) {
            RendererType.JSON -> renderJson(wormsMap, player)
            RendererType.CONSOLE -> "RendererType.CONSOLE"
            RendererType.TEXT -> "RendererType.TEXT"
            else -> {
                throw Exception("RendererType not recognized: $rendererType")
            }
        }
    }

    private fun renderJson(wormsMap: WormsMap, player: WormsPlayer): String {
        val mapSize = config.mapSize

        var groupedArrayMap = (0 until mapSize).map { x ->
            (0 until mapSize).map { y ->
                wormsMap[Point(x, y)]
            }
        }

        groupedArrayMap = modifyCellsForPlayer(groupedArrayMap)

        val wormGameDetails = WormGameDetails(config, wormsMap, groupedArrayMap, player)

        return gson.toJson(wormGameDetails)
    }

    private fun modifyCellsForPlayer(arrayMap: List<List<MapCell>>): List<List<MapCell>> {
        return arrayMap
    }

}



















