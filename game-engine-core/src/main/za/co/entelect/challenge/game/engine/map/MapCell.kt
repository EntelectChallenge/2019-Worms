package za.co.entelect.challenge.game.engine.map

import za.co.entelect.challenge.game.engine.player.WormsPlayer

class MapCell(var type: CellType) {
    val players: MutableList<WormsPlayer> = mutableListOf()

    val open
        get() = type.open

    val diggable
        get() = type.diggable

    val isEmpty
        get() = players.isEmpty()
}