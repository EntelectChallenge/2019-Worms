package za.co.entelect.challenge.game.delegate.renderer

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class WormsGameDetails(config: GameConfig, wormsMap: WormsMap, player: WormsPlayer) {

    var currentRound: Int = wormsMap.currentRound
    var maxRounds: Int = config.maxRounds
    var mapSize: Int = wormsMap.size
    var currentWormId: Int = player.currentWorm.id
    var selfPlayer: WormsPlayer = player
    var enemyPlayers: List<WormsPlayer> = wormsMap.players.filter { it != player }
    var map: List<List<MapCell>>

    init {
        this.map = modifyCellsForPlayer(wormsMap.cells.chunked(wormsMap.size), player)
    }

    /**
     * Remove/hide cells, values, properties that @player is not allowed to see
     */
    private fun modifyCellsForPlayer(arrayMap: List<List<MapCell>>, player: WormsPlayer): List<List<MapCell>> {
        return arrayMap
    }
}
