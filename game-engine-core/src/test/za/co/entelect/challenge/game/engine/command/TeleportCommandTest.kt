package za.co.entelect.challenge.game.engine.command


import kotlin.test.Test
import za.co.entelect.challenge.game.contracts.exceptions.InvalidCommandException
import za.co.entelect.challenge.game.engine.entities.WormsMap
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TeleportCommandTest {

    @Test(expected = InvalidCommandException::class)
    fun test_apply_invalidType() {
        val testCommand = TeleportCommand(1, 1)
        val worm = CommandoWorm(10, Point(0, 0))
        val player = WormsPlayer(listOf(worm))

        val testMap = WormsMap(listOf(player), 2, 2, buildMapCells(4, CellType.DIRT))

        assertFalse(testCommand.isValid(testMap, worm))
        testCommand.execute(testMap, worm)
    }

    @Test
    fun test_apply_valid() {
        val startingPosition = Point(0, 0)
        val targetPosition = Point(1, 1)

        val testCommand = TeleportCommand(targetPosition)
        val worm = CommandoWorm(10, startingPosition)
        val player = WormsPlayer(listOf(worm))
        val testMap = WormsMap(listOf(player), 2, 2, buildMapCells(4,  CellType.AIR ))

        assertTrue(testCommand.isValid(testMap, worm))
        testCommand.execute(testMap, worm)

        assertTrue(testCommand.isValid(testMap, worm))
        assertEquals(testCommand.target, worm.position)
        assertEquals(testMap[testCommand.target].occupier, worm)
        assertEquals(0, worm.roundMoved)
        assertEquals(startingPosition, worm.previousPosition)
    }

    @Test(expected = InvalidCommandException::class)
    fun test_apply_nonEmpty() {
        val testCommand = TeleportCommand(1, 1)
        val worm = CommandoWorm(10, Point(0, 0))
        val player = WormsPlayer(listOf(worm))
        val testMap = WormsMap(listOf(player), 2, 2, buildMapCells(4, CellType.AIR))

        assertFalse(testCommand.isValid(testMap, worm))
        testCommand.execute(testMap, worm)
    }

    @Test
    fun test_apply_pushback() {
        val testCommand = TeleportCommand(Point(1, 1))
        val wormA = CommandoWorm(10, Point(0, 0))
        val wormB = CommandoWorm(10, Point(2, 1))
        val player = WormsPlayer(listOf(wormA))
        val testMap = WormsMap(listOf(player), 2, 2, buildMapCells(4, CellType.AIR))

        testCommand.execute(testMap, wormA)
        testCommand.execute(testMap, wormB)

        assertFalse(testMap[1,1].occupied)
        assertTrue(testMap[0,0].occupied)
        assertTrue(testMap[2,1].occupied)
    }

    @Test
    fun test_apply_tooFar() {
        val worm = CommandoWorm(10, Point(2, 2))
        val player = WormsPlayer(listOf(worm))
        val testMap = WormsMap(listOf(player), 3, 3, buildMapCells(25, CellType.AIR))

        assertFalse(TeleportCommand(0, 0).isValid(testMap, worm))
        assertFalse(TeleportCommand(0, 1).isValid(testMap, worm))
        assertFalse(TeleportCommand(0, 2).isValid(testMap, worm))
        assertFalse(TeleportCommand(0, 3).isValid(testMap, worm))
        assertFalse(TeleportCommand(0, 4).isValid(testMap, worm))

        assertFalse(TeleportCommand(4, 0).isValid(testMap, worm))
        assertFalse(TeleportCommand(4, 1).isValid(testMap, worm))
        assertFalse(TeleportCommand(4, 2).isValid(testMap, worm))
        assertFalse(TeleportCommand(4, 3).isValid(testMap, worm))
        assertFalse(TeleportCommand(4, 4).isValid(testMap, worm))

        assertFalse(TeleportCommand(1, 0).isValid(testMap, worm))
        assertFalse(TeleportCommand(2, 0).isValid(testMap, worm))
        assertFalse(TeleportCommand(3, 0).isValid(testMap, worm))

        assertFalse(TeleportCommand(1, 4).isValid(testMap, worm))
        assertFalse(TeleportCommand(2, 4).isValid(testMap, worm))
        assertFalse(TeleportCommand(3, 4).isValid(testMap, worm))

        assertTrue(TeleportCommand(1, 1).isValid(testMap, worm))
        assertTrue(TeleportCommand(1, 2).isValid(testMap, worm))
        assertTrue(TeleportCommand(1, 3).isValid(testMap, worm))
        assertTrue(TeleportCommand(2, 1).isValid(testMap, worm))

        assertTrue(TeleportCommand(2, 3).isValid(testMap, worm))
        assertTrue(TeleportCommand(3, 1).isValid(testMap, worm))
        assertTrue(TeleportCommand(3, 2).isValid(testMap, worm))
        assertTrue(TeleportCommand(3, 3).isValid(testMap, worm))
    }

    private fun buildMapCells(count: Int, cellType: CellType) = (0..count).map { MapCell(cellType) }.toMutableList()
}
