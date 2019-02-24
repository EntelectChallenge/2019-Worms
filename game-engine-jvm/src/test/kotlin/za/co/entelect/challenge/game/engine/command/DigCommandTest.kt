package za.co.entelect.challenge.game.engine.command

import kotlin.test.Test
import za.co.entelect.challenge.game.engine.entities.GameConfig
import za.co.entelect.challenge.game.engine.entities.WormsMap
import za.co.entelect.challenge.game.engine.exception.InvalidCommandException
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DigCommandTest {

    val config: GameConfig = GameConfig()

    @Test(expected = InvalidCommandException::class)
    fun test_apply_invalidType_Air() {
        val testCommand = DigCommand(1, 1)
        val worm = CommandoWorm.build(config, Point(0, 0))
        val player = WormsPlayer(listOf(worm))

        val testMap = WormsMap(listOf(player), 2, 2, buildMapCells(4, CellType.AIR))

        assertFalse(testCommand.validate(testMap, worm).isValid)
        testCommand.execute(testMap, worm)
    }

    @Test(expected = InvalidCommandException::class)
    fun test_apply_invalidType_Bedrock() {
        val testCommand = DigCommand(1, 1)
        val worm = CommandoWorm.build(config, Point(0, 0))
        val player = WormsPlayer(listOf(worm))

        val testMap = WormsMap(listOf(player), 2, 2, buildMapCells(4, CellType.BEDROCK))

        assertFalse(testCommand.validate(testMap, worm).isValid)
        testCommand.execute(testMap, worm)
    }

    @Test
    fun test_apply_valid() {
        val testCommand = DigCommand(1, 1)
        val worm = CommandoWorm.build(config, Point(0, 0))
        val player = WormsPlayer(listOf(worm))
        val testMap = WormsMap(listOf(player), 2, 2, buildMapCells(4, CellType.DIRT))

        assertTrue(testCommand.validate(testMap, worm).isValid)
        testCommand.execute(testMap, worm)

        assertEquals(testMap[testCommand.target].type, CellType.AIR)
    }

    @Test
    fun test_apply_tooFar() {
        val worm = CommandoWorm.build(config, Point(2, 2))
        val player = WormsPlayer(listOf(worm))
        val testMap = WormsMap(listOf(player), 3, 3, buildMapCells(25, CellType.DIRT))

        for (i in 0..4) {
            assertFalse(DigCommand(0, i).validate(testMap, worm).isValid, "(0, $i) out of range")
            assertFalse(DigCommand(4, i).validate(testMap, worm).isValid, "(4, $i) out of range")
            assertFalse(DigCommand(i, 0).validate(testMap, worm).isValid, "($i, 0) out of range")
            assertFalse(DigCommand(i, 4).validate(testMap, worm).isValid, "($i, 4) out of range")
        }

        for (x in 1..3) {
            for (y in 1..3) {
                assertTrue(DigCommand(x, y).validate(testMap, worm).isValid, "($x, $y) in range")
            }
        }
    }

    private fun buildMapCells(count: Int, cellType: CellType) = (0..count).map { MapCell(cellType) }.toMutableList()


}