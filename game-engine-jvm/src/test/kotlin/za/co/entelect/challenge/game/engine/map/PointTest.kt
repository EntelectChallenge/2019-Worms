package za.co.entelect.challenge.game.engine.map

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PointTest {

    @Test
    fun test_properties() {
        val pointA = Point(0, 0)
        val pointB = Point(10, 10)
        val pointC = Point(-5, -5)

        assertEquals(pointA.euclideanDistance(pointB), 14.142135623730951)
        assertEquals(pointA.movementDistance(pointB), 10)
        assertEquals(pointA.shootingDistance(pointB), 14.0)
        assertEquals(pointA.manhattanDistance(pointB), 20)

        assertNotEquals(pointC.abs(), pointC)
        assertEquals(pointA.plus(pointB), pointB)
        assertEquals(pointA.minus(pointB), Point(-10,-10))
        assertEquals(pointA.toString(), "(0, 0)")
    }

}
