package za.co.entelect.challenge.game.engine.map

import za.co.entelect.challenge.game.engine.player.Worm

class MapCell(var type: CellType) {
    var occupier: Worm? = null

    fun isOccupied(): Boolean {
        return occupier != null
    }
}