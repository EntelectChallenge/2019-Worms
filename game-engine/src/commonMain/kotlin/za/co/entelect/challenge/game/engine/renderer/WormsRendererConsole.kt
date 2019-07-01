package za.co.entelect.challenge.game.engine.renderer

import za.co.entelect.challenge.game.engine.command.implementation.Direction
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.renderer.WormsRenderer.Companion.EOL
import za.co.entelect.challenge.game.engine.renderer.printables.PrintableMapCell.Companion.getStringMap


class WormsRendererConsole(private val config: GameConfig) : WormsRenderer {

    override fun commandPrompt(wormsPlayer: WormsPlayer): String {
        val directionsString = Direction.values().joinToString(", ") { it.shortCardinal }
        return """Player ${wormsPlayer.id}, enter a command (move x y)/(dig x y)/(shoot $directionsString)/(nothing)"""
    }

    override fun render(wormsMap: WormsMap, player: WormsPlayer?): String {
        val wormPosition = player!!.currentWorm.position
        val wormGameDetails = WormsGameDetails(config, wormsMap, player)
        val selfPlayer = "My Player:H=${wormGameDetails.myPlayer?.consoleHealth} S=${wormGameDetails.myPlayer?.score} " +
                "W=${wormGameDetails.currentWormId} X,Y=${wormPosition.x},${wormPosition.y}"

        val enemyPlayers = wormGameDetails.opponents.fold("") { sum, p ->
            sum + "Player ${p.id} :H=${p.consoleHealth} S=${p.score}" + EOL
        }

        val header = """
            |$selfPlayer
            |$enemyPlayers
            """.trimMargin()
        val map = getStringMap(wormGameDetails.map)

        return """
            |$header
            |$map
            """.trimMargin()
    }

}
