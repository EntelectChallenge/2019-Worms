package za.co.entelect.challenge.game.engine.command.implementation

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

        /**
         * Resolves a Direction from the shortened version
         * @param shortened Shortened form of a direction. Should correspond to the `shortened` field of a direction
         *
         * @throws NoSuchElementException If no Direction with the short form exists
         */
        fun fromShortened(shortened: String): Direction = Direction.values().first { it.shortened == shortened.toUpperCase() }

        /**
         * Checks if a Direction with the shortened version exists
         * @param shortened Shortened form of a direction. Should correspond to the `shortened` field of a direction
         * @return true if s matching direction exists, false otherwise
         */
        fun containsShortened(shortened: String): Boolean = Direction.values().any { it.shortened == shortened.toUpperCase() }
    }
}