package za.co.entelect.challenge.game.renderer

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class WormGameDetails(config: GameConfig, wormsMap: WormsMap, player: WormsPlayer) {

    var currentRound: Int
    var maxRounds: Int
    var mapSize: Int
    var currentWormId: Int = player.currentWorm.id
    var selfPlayer: WormsPlayer = player
    var enemyPlayers: List<WormsPlayer>
    var map: List<List<MapCell>>

    init {
        val mapSize = config.mapSize
        var groupedArrayMap = (0 until mapSize).map { x ->
            (0 until mapSize).map { y ->
                wormsMap[Point(x, y)]
            }
        }
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
