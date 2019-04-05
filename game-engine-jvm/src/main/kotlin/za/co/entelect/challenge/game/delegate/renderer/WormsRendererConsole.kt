package za.co.entelect.challenge.game.delegate.renderer

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer


class WormsRendererConsole(private val config: GameConfig) : WormsRenderer {

    override fun commandPrompt(player: WormsPlayer): String {
        return "Player ${player.id}, enter a command (move x y)/(dig x y)/(shoot U/UR/R/DR/D/DL/L/UL)/(nothing)"
    }

    override fun render(wormsMap: WormsMap, player: WormsPlayer): String {
        val wormGameDetails = WormsGameDetails(config, wormsMap, player)
        val selfPlayer = "My Player:H=${wormGameDetails.myPlayer.consoleHealth} S=${wormGameDetails.myPlayer.score} " +
                "W=${wormGameDetails.currentWormId}"

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
