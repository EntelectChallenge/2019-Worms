package za.co.entelect.challenge.game.delegate.renderer

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.powerups.HealthPack


class WormsRendererConsole(private val config: GameConfig) : WormsRenderer {

    private val EOL = System.getProperty("line.separator")

    override fun commandPrompt(wormsPlayer: WormsPlayer): String {
        return "Player ${wormsPlayer.id}, enter a command (move x y)/(dig x y)/(shoot U/UR/R/DR/D/DL/L/UL)/(nothing)"
    }

    override fun render(wormsMap: WormsMap, player: WormsPlayer): String {
        val wormGameDetails = WormsGameDetails(config, wormsMap, player)
        val selfPlayer = "Self:     H=${wormGameDetails.selfPlayer.health} S=${wormGameDetails.selfPlayer.score} " +
                "W=${wormGameDetails.currentWormId}"

        val enemyPlayers = wormGameDetails.enemyPlayers.fold("") { sum, p ->
            sum + "Player ${p.id}: H=${p.health} S=${p.score}" + EOL
        }

        val header = """
            |$selfPlayer
            |$enemyPlayers
            """.trimMargin()
        val map = WormsRendererHelper.getStringMap(wormGameDetails.map)

        return """
            |$header
            |$map
            """.trimMargin()
    }

}
