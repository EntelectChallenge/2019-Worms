package za.co.entelect.challenge.game.engine.renderer

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.renderer.WormsRenderer.Companion.EOL

/**
 * Renders current player state as a line of a CSV file
 *
 * If the current round is 1, a header line will also be returned
 */
class WormsRendererCsv(val config: GameConfig) : WormsRenderer {

    private val standardHeaders = listOf("Round", "LastCommandType", "LastCommand", "ActiveWorm", "Score", "Health")

    override fun commandPrompt(wormsPlayer: WormsPlayer): String {
        return "Not implemented for the csv state file"
    }

    override fun render(wormsMap: WormsMap, player: WormsPlayer?): String {
        if (player == null) {
            throw UnsupportedOperationException("Cannot call CSV Render with a null player parameter")
        }

        val header = if (wormsMap.currentRound == 1) {
            val wormHeaders = player.worms.flatMap { listOf("Worm${it.id} Health", "Worm${it.id} x", "Worm${it.id} y") }
            (standardHeaders + wormHeaders).joinToString(separator = config.csvSeparator, postfix = EOL)
        } else ""

        val command = wormsMap.getFeedback(wormsMap.currentRound - 1)
                .firstOrNull { it.playerId == player.id && !it.command.startsWith("select") }?.command
        val commandType = command?.run {
            val firstSpace = this.indexOf(' ')
            if (firstSpace != -1) {
                this.substring(0, firstSpace)
            } else this
        }

        val standardFields = listOf(wormsMap.currentRound, commandType, "\"$command\"", player.previousWorm.id, player.totalScore, player.health)
        val wormFields = player.worms.flatMap { listOf(it.health, it.position.x, it.position.y) }

        val values = (standardFields + wormFields).joinToString(separator = config.csvSeparator)

        return header + values
    }

}
