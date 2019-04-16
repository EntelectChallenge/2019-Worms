package za.co.entelect.challenge.game.engine.command.implementation

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.factory.TestMapFactory.buildMapWithCellType
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DigCommandTest {

    val config: GameConfig = TEST_CONFIG

    @Test
    fun test_apply_outOfRange() {
        val testCommand = DigCommand(4, 4, TEST_CONFIG)
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        val player = WormsPlayer.build(0, listOf(worm), config)

        val testMap = buildMapWithCellType(listOf(player), 4, CellType.DIRT)

        assertFalse(testCommand.validate(testMap, worm).isValid)
    }

    @Test
    fun test_apply_invalidType_Air() {
        val testCommand = DigCommand(1, 1, TEST_CONFIG)
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        val player = WormsPlayer.build(0, listOf(worm), config)

        val testMap = buildMapWithCellType(listOf(player), 2, CellType.AIR)

        assertFalse(testCommand.validate(testMap, worm).isValid)
    }

    @Test
    fun test_apply_invalidType_Bedrock() {
        val testCommand = DigCommand(1, 1, TEST_CONFIG)
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        val player = WormsPlayer.build(0, listOf(worm), config)

        val testMap = buildMapWithCellType(listOf(player), 2, CellType.DEEP_SPACE)

        assertFalse(testCommand.validate(testMap, worm).isValid)
    }

    @Test
    fun test_apply_valid() {
        val testCommand = DigCommand(1, 1, TEST_CONFIG)
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        val player = WormsPlayer.build(0, listOf(worm), config)

        val testMap = buildMapWithCellType(listOf(player), 2, CellType.DIRT)

        assertTrue(testCommand.validate(testMap, worm).isValid)
        testCommand.execute(testMap, worm)

        assertEquals(testMap[testCommand.target].type, CellType.AIR)
    }

    @Test
    fun test_apply_tooFar() {
        val worm = CommandoWorm.build(0, config, Point(2, 2))
        val player = WormsPlayer.build(0, listOf(worm), config)
        val testMap = buildMapWithCellType(listOf(player), 5, CellType.DIRT)

        for (i in 0..4) {
            assertFalse(DigCommand(0, i, TEST_CONFIG).validate(testMap, worm).isValid, "(0, $i) out of range")
            assertFalse(DigCommand(4, i, TEST_CONFIG).validate(testMap, worm).isValid, "(4, $i) out of range")
            assertFalse(DigCommand(i, 0, TEST_CONFIG).validate(testMap, worm).isValid, "($i, 0) out of range")
            assertFalse(DigCommand(i, 4, TEST_CONFIG).validate(testMap, worm).isValid, "($i, 4) out of range")
        }

        for (x in 1..3) {
            for (y in 1..3) {
                assertTrue(DigCommand(x, y, TEST_CONFIG).validate(testMap, worm).isValid, "($x, $y) in range")
            }
        }
    }
}
