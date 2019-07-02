package za.co.entelect.challenge.game.engine.renderer.printables

import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.powerups.Powerup
import za.co.entelect.challenge.game.engine.renderer.WormsRenderer

class PrintableMapCell(cell: MapCell) {

    val x: Int = cell.x
    val y: Int = cell.y
    val type: CellType = cell.type
    var occupier: PrintableWorm? = null
    val powerup: Powerup? = cell.powerup

    companion object {
        /**
         * Build a PrintableMapCell from @cell that is modified to fit the perspective of @perspectivePlayer
         */
        fun buildForPerspectivePlayer(cell: MapCell, perspectivePlayer: WormsPlayer?): PrintableMapCell {
            val cellForPerspectivePlayer = PrintableMapCell(cell)

            if (cell.isOccupied() && !(cell.occupier!!.dead)) {
                cellForPerspectivePlayer.occupier = PrintableWorm.buildForMapPerspectivePlayer(
                        cell.occupier!!, perspectivePlayer)
            }
            return cellForPerspectivePlayer
        }

        fun getStringMap(arrayMap: List<List<PrintableMapCell>>): String {
            return arrayMap.joinToString(WormsRenderer.EOL) {
                it.joinToString("") { cell ->
                    when {
                        cell.powerup != null -> cell.powerup.printable
                        cell.occupier != null -> cell.occupier?.printable.toString()
                        else -> cell.type.printable
                    }
                }
            }
        }

    }

}
