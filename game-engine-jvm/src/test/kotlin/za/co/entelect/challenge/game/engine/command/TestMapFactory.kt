package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.MapCell

object TestMapFactory {
    fun buildMapCells(count: Int, cellType: CellType) = (0..count).map { MapCell(cellType) }.toMutableList()
}