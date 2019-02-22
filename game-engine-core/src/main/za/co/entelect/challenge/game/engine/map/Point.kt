package za.co.entelect.challenge.game.engine.map

import kotlin.math.abs
import kotlin.math.max

data class Point(val x: Int, val y: Int) {

    /**
     * Movement is vertical, horizontal or diagonal so this is the maximum of x and y distance
     */
    fun movementDistance(other: Point): Int {
        return max(
                abs(x - other.x),
                abs(y - other.y)
        )
    }
}