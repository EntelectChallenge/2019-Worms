package za.co.entelect.challenge.game.engine.entities

import za.co.entelect.challenge.game.engine.map.Point

enum class Direction(x: Int, y: Int) {

    UP(0, -1),
    UP_RIGHT(1, -1),
    RIGHT(1, 0),
    DOWN_RIGHT(1, 1),
    DOWN(0, 1),
    DOWN_LEFT(-1, 1),
    LEFT(-1, 0),
    UP_LEFT(-1, -1);

    val vector = Point(x, y)
}