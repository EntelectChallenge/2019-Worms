package za.co.entelect.challenge.game.engine.map

import za.co.entelect.challenge.game.engine.player.Worm

class MapCell(var type: CellType) {
    var occupier: Worm? = null

    val open
        get() = type.movable

    val diggable
        get() = type.diggable

    val occupied
        get() = occupier != null
}