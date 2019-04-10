package za.co.entelect.challenge.game.delegate.renderer.printables

import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.powerups.Powerup

class PrintableMapCell(cell: MapCell) {

    var x: Int = cell.x
    var y: Int = cell.y
    var type: CellType = cell.type
    var occupier: PrintableWorm? = null
    var powerup: Powerup? = cell.powerup

    companion object {
        /**
         * Build a PrintableMapCell from @cell that is modified to fit the perspective of @perspectivePlayer
         */
        fun buildForPerspectivePlayer(cell: MapCell, perspectivePlayer: WormsPlayer): PrintableMapCell {
            val cellForPerspectivePlayer = PrintableMapCell(cell)

            if (cell.isOccupied() && !(cell.occupier!!.dead)) {
                cellForPerspectivePlayer.occupier = PrintableWorm.buildForMapPerspectivePlayer(
                        cell.occupier!!, perspectivePlayer)
            }
            return cellForPerspectivePlayer
        }
    }

}
