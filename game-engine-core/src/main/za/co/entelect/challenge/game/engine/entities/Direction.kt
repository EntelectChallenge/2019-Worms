package za.co.entelect.challenge.game.engine.entities

import za.co.entelect.challenge.game.engine.map.Point

enum class Direction(val shortened: String, x: Int, y: Int) {

    UP("U", 0, -1),
    UP_RIGHT("UR", 1, -1),
    RIGHT("R", 1, 0),
    DOWN_RIGHT("DR",1, 1),
    DOWN("D", 0, 1),
    DOWN_LEFT("DL", -1, 1),
    LEFT("L", -1, 0),
    UP_LEFT("UL", -1, -1);

    val vector = Point(x, y)

    companion object {
        fun fromShortened(shortened: String): Direction = Direction.values().first { it.shortened == shortened }

        fun containsShortened(shortened: String): Boolean = Direction.values().any { it.shortened == shortened }
    }
}