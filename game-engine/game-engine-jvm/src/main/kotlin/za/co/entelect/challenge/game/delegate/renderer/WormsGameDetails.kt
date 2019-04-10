package za.co.entelect.challenge.game.delegate.renderer

import za.co.entelect.challenge.game.delegate.renderer.printables.PrintableMapCell
import za.co.entelect.challenge.game.delegate.renderer.printables.PrintablePlayer
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class WormsGameDetails(config: GameConfig, wormsMap: WormsMap, player: WormsPlayer) {

    var currentRound: Int = wormsMap.currentRound
    var maxRounds: Int = config.maxRounds
    var mapSize: Int = wormsMap.size
    var currentWormId: Int = player.currentWorm.id
    var consecutiveDoNothingCount: Int = player.consecutiveDoNothingsCount
    var myPlayer: PrintablePlayer = PrintablePlayer.buildForPerspectivePlayer(player, player)
    var opponents: List<PrintablePlayer> = wormsMap.players
            .filter { it != player }
            .map { PrintablePlayer.buildForPerspectivePlayer(it, player) }
    var map: List<List<PrintableMapCell>> = modifyCellsForPlayer(wormsMap.cells, player).chunked(wormsMap.size)

    /**
     * Remove/hide cells, values, properties that @player is not allowed to see
     */
    private fun modifyCellsForPlayer(arrayMap: List<MapCell>, player: WormsPlayer): List<PrintableMapCell> {
        return arrayMap.map { PrintableMapCell.buildForPerspectivePlayer(it, player) }
    }

}
