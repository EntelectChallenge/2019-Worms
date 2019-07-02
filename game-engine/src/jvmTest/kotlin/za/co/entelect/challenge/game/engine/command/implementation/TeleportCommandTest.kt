package za.co.entelect.challenge.game.engine.command.implementation

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.command.CommandStrings
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.factory.TestMapFactory.buildMapWithCellType
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.Worm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.powerups.Powerup
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TeleportCommandTest {

    val config: GameConfig = TEST_CONFIG

    @Test
    fun test_apply_outOfRange() {
        val testCommand = TeleportCommand(4, 4, Random, config)
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        val player = WormsPlayer.build(0, listOf(worm), config)

        val testMap = buildMapWithCellType(listOf(player), 4, CellType.AIR)

        assertFalse(testCommand.validate(testMap, worm).isValid)
    }

    @Test
    fun test_apply_invalidType() {
        val testCommand = TeleportCommand(1, 1, Random, config)
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        val player = WormsPlayer.build(0, listOf(worm), config)

        val testMap = buildMapWithCellType(listOf(player), 2, CellType.DIRT)

        assertFalse(testCommand.validate(testMap, worm).isValid)
    }

    @Test
    fun test_apply_valid() {
        val startingPosition = Point(0, 0)
        val targetPosition = Point(1, 1)

        val testCommand = TeleportCommand(targetPosition, Random, config)
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        val player = WormsPlayer.build(0, listOf(worm), config)
        val testMap = buildMapWithCellType(listOf(player), 2, CellType.AIR)

        assertTrue(testCommand.validate(testMap, worm).isValid)
        testCommand.execute(testMap, worm)

        assertEquals(testCommand.target, worm.position)
        assertEquals(testMap[testCommand.target].occupier, worm)
        assertEquals(0, worm.roundMoved)
        assertEquals(startingPosition, worm.previousPosition)
    }

    @Test
    fun test_apply_twice() {
        val startingPosition = Point(0, 0)
        val targetPosition = Point(1, 1)

        val testCommand = TeleportCommand(targetPosition, Random, config)
        val worm = CommandoWorm.build(0, config, startingPosition)
        val player = WormsPlayer.build(0, listOf(worm), config)
        val testMap = buildMapWithCellType(listOf(player), 2, CellType.AIR)

        assertTrue(testCommand.validate(testMap, worm).isValid)
        testCommand.execute(testMap, worm)

        assertEquals(testCommand.target, worm.position)
        assertEquals(testMap[testCommand.target].occupier, worm)
        assertEquals(0, worm.roundMoved)
        assertEquals(startingPosition, worm.previousPosition)

        assertFalse(testCommand.validate(testMap, worm).isValid)
    }

    @Test
    fun test_apply_nonEmpty() {
        val testCommand = TeleportCommand(1, 1, Random, config)
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        val player = WormsPlayer.build(0, listOf(worm), config)
        val testMap = buildMapWithCellType(listOf(player), 2, CellType.AIR)

        testMap[1, 1].occupier = CommandoWorm.build(0, config, Point(0, 0))

        assertFalse(testCommand.validate(testMap, worm).isValid)
    }

    /**
     * When two worms move to the same cell in the same round
     */
    @Test
    fun test_apply_collide_pushback() {
        val random: Random = mock {
            on { nextBoolean() } doReturn true
        }

        val testCommand = TeleportCommand(Point(1, 1), random, config)
        val wormA = CommandoWorm.build(0, config, Point(0, 0))
        val wormB = CommandoWorm.build(0, config, Point(2, 1))
        val player = WormsPlayer.build(0, listOf(wormA, wormB), config)
        val testMap = buildMapWithCellType(listOf(player), 4, CellType.AIR)

        assertTrue(testCommand.validate(testMap, wormA).isValid, "Command A Valid")
        testCommand.execute(testMap, wormA)
        assertTrue(testCommand.validate(testMap, wormB).isValid, "Command B Valid")
        testCommand.execute(testMap, wormB)

        assertFalse(testMap[1, 1].isOccupied(), "Target not occupied")
        assertTrue(testMap[0, 0].isOccupied())
        assertTrue(testMap[2, 1].isOccupied())
        assertEquals(wormA, testMap[0, 0].occupier)
        assertEquals(wormB, testMap[2, 1].occupier)
    }

    /**
     * When two worms move to the same cell in the same round
     */
    @Test
    fun test_apply_collide_swap() {
        val random: Random = mock {
            on { nextBoolean() } doReturn false
        }

        val testCommand = TeleportCommand(Point(1, 1), random, config)

        val wormA = CommandoWorm.build(0, config, Point(0, 0))
        val wormB = CommandoWorm.build(0, config, Point(2, 1))
        val playerA = WormsPlayer.build(0, listOf(wormA, wormB), config)
        val testMap = buildMapWithCellType(listOf(playerA), 3, CellType.AIR)

        assertTrue(testCommand.validate(testMap, wormA).isValid, "Command A Valid")
        testCommand.execute(testMap, wormA)
        assertTrue(testCommand.validate(testMap, wormB).isValid, "Command B Valid")
        testCommand.execute(testMap, wormB)

        assertFalse(testMap[1, 1].isOccupied(), "Target not occupied")
        assertTrue(testMap[0, 0].isOccupied())
        assertTrue(testMap[2, 1].isOccupied())

        assertEquals(wormB, testMap[0, 0].occupier)
        assertEquals(wormA, testMap[2, 1].occupier)
    }

    @Test
    fun test_apply_tooFar() {
        val worm = CommandoWorm.build(0, config, Point(2, 2))
        val player = WormsPlayer.build(0, listOf(worm), config)
        val testMap = buildMapWithCellType(listOf(player), 5, CellType.AIR)

        for (i in 0..4) {
            validateOutRangeFeedback(0, i, testMap, worm)
            validateOutRangeFeedback(4, i, testMap, worm)
            validateOutRangeFeedback(i, 0, testMap, worm)
            validateOutRangeFeedback(i, 4, testMap, worm)
        }

        for (x in 1..3) {
            for (y in 1..3) {
                if (x == 2 && y == 2) {
                    assertFalse(TeleportCommand(x, y, Random, config).validate(testMap, worm).isValid, "($x, $y) occupied")
                } else {
                    assertTrue(TeleportCommand(x, y, Random, config).validate(testMap, worm).isValid, "($x, $y) in range")
                }
            }
        }
    }

    private fun validateOutRangeFeedback(x: Int, y: Int, testMap: WormsMap, worm: Worm) {
        val command = TeleportCommand(x, y, Random, TEST_CONFIG)
        assertFalse(command.validate(testMap, worm).isValid, "($x, $y) out of range")
        assertEquals(command.toString(), "${CommandStrings.MOVE.string} ($x, $y)")
    }

}
