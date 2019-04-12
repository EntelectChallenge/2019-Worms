package za.co.entelect.challenge.game.engine.factory

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.math.floor
import kotlin.math.sqrt

object TestMapFactory {

    fun buildMapCells(count: Int, cellType: CellType): MutableList<MapCell> {
        val mapSize = sqrt(count.toDouble()).toInt()
        return (0 until count).map {
            MapCell(it % mapSize,
                    floor((it / mapSize).toDouble()).toInt(),
                    cellType)
        }.toMutableList()
    }

    fun buildMapWithCellType(players: List<WormsPlayer>, size: Int, cellType: CellType): WormsMap {
        return WormsMap(players, size, buildMapCells(size * size, cellType))
    }


    fun getMapCenter(config: GameConfig): Pair<Double, Double> =
            Pair((((config.mapSize + 1) / 2) - 0.5), ((config.mapSize + 1) / 2) - 0.5)

    fun standardDeviation(elements: List<Double>): Double {
        val mean = elements.sorted().get(elements.size / 2)
        val deviationSquared = elements.fold(0.0) { sum, i -> sum + Math.pow(i - mean, 2.0) }
        return sqrt(deviationSquared / (elements.size - 1))
    }

}
