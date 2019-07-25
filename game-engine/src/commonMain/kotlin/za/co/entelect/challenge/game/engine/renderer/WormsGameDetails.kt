package za.co.entelect.challenge.game.engine.renderer

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.renderer.printables.PrintableMapCell
import za.co.entelect.challenge.game.engine.renderer.printables.PrintablePlayer
import za.co.entelect.challenge.game.engine.renderer.printables.PrintableVisualizerEvent
import za.co.entelect.challenge.game.engine.command.feedback.CommandFeedback

class WormsGameDetails(config: GameConfig, wormsMap: WormsMap, player: WormsPlayer?) {

    val currentRound: Int = wormsMap.currentRound
    val maxRounds: Int = config.maxRounds
    val pushbackDamage: Int = config.pushbackDamage

    val mapSize: Int = wormsMap.size
    val currentWormId: Int? = player?.currentWorm?.id
    val consecutiveDoNothingCount: Int? = player?.consecutiveDoNothingsCount

    val myPlayer: PrintablePlayer? = when {
        player != null -> PrintablePlayer.buildForPerspectivePlayer(player, player)
        else -> null
    }
    val opponents: List<PrintablePlayer> = wormsMap.players
            .filter { it != player }
            .map { PrintablePlayer.buildForPerspectivePlayer(it, player, wormsMap) }
    val map: List<List<PrintableMapCell>> = modifyCellsForPlayer(wormsMap.cells, player).chunked(wormsMap.size)
    val visualizerEvents: List<PrintableVisualizerEvent>
            = wormsMap.getVisualizerEvents().map { PrintableVisualizerEvent(it) }

    /**
     * Remove/hide cells, values, properties that @player is not allowed to see
     */
    private fun modifyCellsForPlayer(arrayMap: List<MapCell>, player: WormsPlayer?): List<PrintableMapCell> {
        return arrayMap.map { PrintableMapCell.buildForPerspectivePlayer(it, player) }
    }

}
