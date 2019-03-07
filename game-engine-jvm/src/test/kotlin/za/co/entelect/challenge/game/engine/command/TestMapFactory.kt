package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.entities.WormsMap
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.player.WormsPlayer

object TestMapFactory {
    fun buildMapCells(count: Int, cellType: CellType) = (0..count).map { MapCell(cellType) }.toMutableList()

    fun buildMapWithCellType(players: List<WormsPlayer>, rows: Int, columns: Int, cellType: CellType): WormsMap {
        return WormsMap(players, rows, columns, buildMapCells(rows * columns, cellType))
    }
}