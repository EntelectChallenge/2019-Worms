package za.co.entelect.challenge.game.engine.command.implementation

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.command.TestMapFactory.buildMapWithCellType
import za.co.entelect.challenge.game.engine.command.implementation.Direction.*
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShootCommandTest {

    private val config: GameConfig = TEST_CONFIG

    private val initialHp = config.commandoWorms.initialHp
    private val expectedHp = initialHp - config.commandoWorms.weapon.damage

    @Test
    fun test_valid_close() {

        val targetWorms = listOf(
                CommandoWorm.build(0, config, Point(1, 1)),
                CommandoWorm.build(0, config, Point(2, 1)),
                CommandoWorm.build(0, config, Point(3, 1)),
                CommandoWorm.build(0, config, Point(1, 2)),
                CommandoWorm.build(0, config, Point(3, 2)),
                CommandoWorm.build(0, config, Point(1, 3)),
                CommandoWorm.build(0, config, Point(2, 3)),
                CommandoWorm.build(0, config, Point(3, 3))
        )

        val targetPlayer = WormsPlayer.build(0, targetWorms, config)
        val startingPosition = Point(2, 2)
        val attacker = CommandoWorm.build(0, config, startingPosition)
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 5, CellType.AIR)

        val directions = listOf(UP_LEFT, UP, UP_RIGHT,
                LEFT, RIGHT, DOWN_LEFT, DOWN, DOWN_RIGHT)

        for ((index, direction) in directions.withIndex()) {
            testMap.currentRound++
            val testCommand = ShootCommand(direction)

            assertTrue(testCommand.validate(testMap, attacker).isValid)
            testCommand.execute(testMap, attacker)

            assertEquals(testMap.currentRound, targetWorms[index].roundHit, "Hit round for worm in direction $direction")
            assertEquals(expectedHp, targetWorms[index].health, "Health for worm in direction $direction")
        }

        assertEquals(initialHp, attacker.health)
    }

    @Test
    fun test_validFar() {

        val targetWorms = listOf(
                CommandoWorm.build(0, config, Point(0, 0)),
                CommandoWorm.build(0, config, Point(2, 0)),
                CommandoWorm.build(0, config, Point(4, 0)),
                CommandoWorm.build(0, config, Point(0, 2)),
                CommandoWorm.build(0, config, Point(4, 2)),
                CommandoWorm.build(0, config, Point(0, 4)),
                CommandoWorm.build(0, config, Point(2, 4)),
                CommandoWorm.build(0, config, Point(4, 4))
        )

        val targetPlayer = WormsPlayer.build(0, targetWorms, config)
        val startingPosition = Point(2, 2)
        val attacker = CommandoWorm.build(0, config, startingPosition)
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 5, CellType.AIR)

        val directions = listOf(UP_LEFT, UP, UP_RIGHT,
                LEFT, RIGHT, DOWN_LEFT, DOWN, DOWN_RIGHT)

        for ((index, direction) in directions.withIndex()) {
            testMap.currentRound++
            val testCommand = ShootCommand(direction)

            assertTrue(testCommand.validate(testMap, attacker).isValid)
            testCommand.execute(testMap, attacker)

            assertEquals(testMap.currentRound, targetWorms[index].roundHit, "Hit round for worm in direction $direction")
            assertEquals(expectedHp, targetWorms[index].health, "Health for worm in direction $direction")
        }

        assertEquals(initialHp, attacker.health)
    }

    @Test
    fun test_valid_friendlyFire() {

        val targetWorms = listOf(
                CommandoWorm.build(0, config, Point(1, 1)),
                CommandoWorm.build(0, config, Point(2, 1)),
                CommandoWorm.build(0, config, Point(3, 1)),
                CommandoWorm.build(0, config, Point(1, 2)),
                CommandoWorm.build(0, config, Point(3, 2)),
                CommandoWorm.build(0, config, Point(1, 3)),
                CommandoWorm.build(0, config, Point(2, 3)),
                CommandoWorm.build(0, config, Point(3, 3))
        )

        val startingPosition = Point(2, 2)
        val attacker = CommandoWorm.build(0, config, startingPosition)
        val attackingPlayer = WormsPlayer.build(0, targetWorms + attacker, config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer), 5, CellType.AIR)

        val directions = listOf(UP_LEFT, UP, UP_RIGHT,
                LEFT, RIGHT, DOWN_LEFT, DOWN, DOWN_RIGHT)

        for ((index, direction) in directions.withIndex()) {
            testMap.currentRound++
            val testCommand = ShootCommand(direction)

            testCommand.execute(testMap, attacker)

            assertEquals(testMap.currentRound, targetWorms[index].roundHit, "Hit round for worm in direction $direction")
            assertEquals(expectedHp, targetWorms[index].health, "Health for worm in direction $direction")
        }

        assertEquals(initialHp, attacker.health)
    }

    @Test
    fun test_noObstacles() {

        val startingPosition = Point(2, 2)
        val attacker = CommandoWorm.build(0, config, startingPosition)
        val attackingPlayer = WormsPlayer.build(0, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer), 5, CellType.AIR)

        val directions = listOf(UP_LEFT, UP, UP_RIGHT,
                LEFT, RIGHT, DOWN_LEFT, DOWN, DOWN_RIGHT)

        for (direction in directions) {
            testMap.currentRound++
            val testCommand = ShootCommand(direction)

            assertTrue(testCommand.validate(testMap, attacker).isValid)
            testCommand.execute(testMap, attacker)
        }

        assertEquals(initialHp, attacker.health)
    }

    @Test
    fun test_obstacles() {

        val targetWorms = listOf(
                CommandoWorm.build(0, config, Point(0, 0)),
                CommandoWorm.build(0, config, Point(2, 0)),
                CommandoWorm.build(0, config, Point(4, 0)),
                CommandoWorm.build(0, config, Point(0, 2)),
                CommandoWorm.build(0, config, Point(4, 2)),
                CommandoWorm.build(0, config, Point(0, 4)),
                CommandoWorm.build(0, config, Point(2, 4)),
                CommandoWorm.build(0, config, Point(4, 4))
        )

        val targetPlayer = WormsPlayer.build(0, targetWorms, config)
        val startingPosition = Point(2, 2)
        val attacker = CommandoWorm.build(0, config, startingPosition)
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 5, CellType.AIR)
        val directions = listOf(UP_LEFT, UP, UP_RIGHT,
                LEFT, RIGHT, DOWN_LEFT, DOWN, DOWN_RIGHT)

        for (i in 1..3) {
            testMap[1, i].type = CellType.DIRT
            testMap[i, 1].type = CellType.DIRT
            testMap[3, i].type = CellType.DIRT
            testMap[i, 3].type = CellType.DIRT
        }

        for (direction in directions) {
            testMap.currentRound++
            val testCommand = ShootCommand(direction)

            assertTrue(testCommand.validate(testMap, attacker).isValid)
            testCommand.execute(testMap, attacker)
        }

        assertEquals(initialHp, attacker.health)
    }

    @Test
    fun test_wormAtMaxRange() {

        val targetWorms = listOf(
                CommandoWorm.build(0, config, Point(0, 0))
        )

        val targetPlayer = WormsPlayer.build(0, targetWorms, config)
        val startingPosition = Point(3, 3)
        val attacker = CommandoWorm.build(0, config, startingPosition)
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 5, CellType.AIR)

        val testCommand = ShootCommand(UP_LEFT)

        assertTrue(testCommand.validate(testMap, attacker).isValid)
        testCommand.execute(testMap, attacker)

        assertEquals(expectedHp, targetWorms[0].health)
        assertEquals(initialHp, attacker.health)
    }

    @Test
    fun test_wormOutOfRange() {

        val targetWorms = listOf(
                CommandoWorm.build(0, config, Point(0, 0))
        )

        val targetPlayer = WormsPlayer.build(0, targetWorms, config)
        val startingPosition = Point(4, 4)
        val attacker = CommandoWorm.build(0, config, startingPosition)
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 5, CellType.AIR)

        val testCommand = ShootCommand(UP_LEFT)

        assertTrue(testCommand.validate(testMap, attacker).isValid)
        testCommand.execute(testMap, attacker)

        assertEquals(initialHp, targetWorms[0].health)
        assertEquals(initialHp, attacker.health)
    }


}