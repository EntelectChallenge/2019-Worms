package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.engine.map.Point

abstract class Worm(var health: Int, var position: Point) {

    val dead: Boolean
        get() = health == 0

}
