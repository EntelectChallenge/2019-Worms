package za.co.entelect.challenge.game.engine.command

import org.junit.Assert.*
import org.junit.Test
import za.co.entelect.challenge.game.contracts.exceptions.InvalidCommandException
import za.co.entelect.challenge.game.engine.entities.WormsMap
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class TeleportCommandTest {

    @Test(expected = InvalidCommandException::class)
    fun test_apply_invalidType() {
        val testCommand = TeleportCommand(1, 1)
        val worm = CommandoWorm(10, Point(0, 0))
        val player = WormsPlayer(listOf(worm))
        val testMap = WormsMap(listOf(player), 2, 2, buildMapCells(CellType.DIRT, CellType.DIRT, CellType.DIRT, CellType.DIRT))


        assertFalse(testCommand.isValid(testMap, worm))
        testCommand.execute(testMap, worm)
    }

    @Test
    fun test_apply_valid() {
        val testCommand = TeleportCommand(1, 1)
        val worm = CommandoWorm(10, Point(0, 0))
        val player = WormsPlayer(listOf(worm))
        val testMap = WormsMap(listOf(player), 2, 2, buildMapCells())

        assertTrue(testCommand.isValid(testMap, worm))
        testCommand.execute(testMap, worm)

        assertEquals(testCommand.target, player.worms[0].position)
    }

    @Test(expected = InvalidCommandException::class)
    fun test_apply_nonEmpty() {
        val testCommand = TeleportCommand(1, 1)
        val worm = CommandoWorm(10, Point(0, 0))
        val player = WormsPlayer(listOf(worm))
        val testMap = WormsMap(listOf(player), 2, 2, buildMapCells())

        testCommand.execute(testMap, worm)
    }

    @Test(expected = InvalidCommandException::class)
    fun test_apply_tooFar() {
        val testCommand = TeleportCommand(1, 1)
        val worm = CommandoWorm(10, Point(0, 0))
        val player = WormsPlayer(listOf(worm))
        val testMap = WormsMap(listOf(player), 2, 2, buildMapCells())

        testCommand.execute(testMap, worm)
    }

    private fun buildMapCells(vararg cells: CellType): MutableList<MapCell> {
        return cells.map {
            MapCell(it)
        }.toMutableList()
    }
}
