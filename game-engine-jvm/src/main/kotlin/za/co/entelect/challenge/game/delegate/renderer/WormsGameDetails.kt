package za.co.entelect.challenge.game.delegate.renderer

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class WormsGameDetails(config: GameConfig, wormsMap: WormsMap, player: WormsPlayer) {

    var currentRound: Int
    var maxRounds: Int
    var mapSize: Int
    var currentWormId: Int = player.currentWorm.id
    var selfPlayer: WormsPlayer = player
    var enemyPlayers: List<WormsPlayer>
    var map: List<List<MapCell>>

    init {
        val mapSize = config.mapSize
        var groupedArrayMap = wormsMap.cells.chunked(wormsMap.size)
        groupedArrayMap = modifyCellsForPlayer(groupedArrayMap, player)
        this.currentRound = wormsMap.currentRound
        this.maxRounds = config.maxRounds
        this.mapSize = config.mapSize
        this.enemyPlayers = wormsMap.players.filter { it != player }
        this.map = groupedArrayMap
    }

    /**
     * Remove/hide cells, values, properties that @player is not allowed to see
     */
    private fun modifyCellsForPlayer(arrayMap: List<List<MapCell>>, player: WormsPlayer): List<List<MapCell>> {
        return arrayMap
    }
}
