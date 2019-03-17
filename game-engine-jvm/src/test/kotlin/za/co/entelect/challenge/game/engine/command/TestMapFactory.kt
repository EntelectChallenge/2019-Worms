package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer

object TestMapFactory {
    fun buildMapCells(count: Int, cellType: CellType) = (0 until count).map { MapCell(cellType) }.toMutableList()

    fun buildMapWithCellType(players: List<WormsPlayer>, size: Int, cellType: CellType): WormsMap {
        return WormsMap(players, size, buildMapCells(size * size, cellType))
    }
}