package za.co.entelect.challenge.game.engine.command

import kotlin.test.Test
import za.co.entelect.challenge.game.contracts.exceptions.InvalidCommandException
import za.co.entelect.challenge.game.engine.entities.WormsMap
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class DigCommandTest {

    @Test(expected = InvalidCommandException::class)
    fun test_apply_invalidType_Air() {
        val testCommand = DigCommand(1, 1)
        val worm = CommandoWorm(10, Point(0, 0))
        val player = WormsPlayer(listOf(worm))

        val testMap = WormsMap(listOf(player), 2, 2, buildMapCells(4, CellType.AIR))

        kotlin.test.assertFalse(testCommand.isValid(testMap, worm))
        testCommand.execute(testMap, worm)
    }

    @Test(expected = InvalidCommandException::class)
    fun test_apply_invalidType_Bedrock() {
        val testCommand = DigCommand(1, 1)
        val worm = CommandoWorm(10, Point(0, 0))
        val player = WormsPlayer(listOf(worm))

        val testMap = WormsMap(listOf(player), 2, 2, buildMapCells(4, CellType.BEDROCK))

        kotlin.test.assertFalse(testCommand.isValid(testMap, worm))
        testCommand.execute(testMap, worm)
    }

    @Test
    fun test_apply_valid() {
        val startingPosition = Point(0, 0)
        val targetPosition = Point(1, 1)

        val testCommand = DigCommand(targetPosition)
        val worm = CommandoWorm(10, startingPosition)
        val player = WormsPlayer(listOf(worm))
        val testMap = WormsMap(listOf(player), 2, 2, buildMapCells(4, CellType.DIRT))

        kotlin.test.assertTrue(testCommand.isValid(testMap, worm))
        testCommand.execute(testMap, worm)

        kotlin.test.assertEquals(testMap[testCommand.target].type, CellType.AIR)
    }

    @Test
    fun test_apply_tooFar() {
        val worm = CommandoWorm(10, Point(2, 2))
        val player = WormsPlayer(listOf(worm))
        val testMap = WormsMap(listOf(player), 3, 3, buildMapCells(25, CellType.DIRT))

        for (i in 0..4) {
            kotlin.test.assertFalse(DigCommand(0, i).isValid(testMap, worm), "(0, $i) out of range")
            kotlin.test.assertFalse(DigCommand(4, i).isValid(testMap, worm), "(4, $i) out of range")
            kotlin.test.assertFalse(DigCommand(i, 0).isValid(testMap, worm), "($i, 0) out of range")
            kotlin.test.assertFalse(DigCommand(i, 4).isValid(testMap, worm), "($i, 4) out of range")
        }

        for (x in 1..3) {
            for (y in 1..3) {
                kotlin.test.assertTrue(DigCommand(x, y).isValid(testMap, worm), "($x, $y) in range")
            }
        }
    }

    private fun buildMapCells(count: Int, cellType: CellType) = (0..count).map { MapCell(cellType) }.toMutableList()


}