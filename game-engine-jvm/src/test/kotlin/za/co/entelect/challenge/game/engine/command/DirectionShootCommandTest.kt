package za.co.entelect.challenge.game.engine.command

import org.junit.Test
import za.co.entelect.challenge.game.engine.command.TestMapFactory.buildMapWithCellType
import za.co.entelect.challenge.game.engine.entities.Direction.*
import za.co.entelect.challenge.game.engine.entities.GameConfig
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class DirectionShootCommandTest {

    private val config: GameConfig = GameConfig()
    private val expectedHp = config.commandoWorms.initialHp - config.commandoWorms.weapon.damage

    //TODO: Test
    // - blocked by dirt
    // - nothing in range

    @Test
    fun test_apply_valid_close() {

        val targetWorms = listOf(
                CommandoWorm.build(config, Point(1, 1)),
                CommandoWorm.build(config, Point(2, 1)),
                CommandoWorm.build(config, Point(3, 1)),
                CommandoWorm.build(config, Point(1, 2)),
                CommandoWorm.build(config, Point(3, 2)),
                CommandoWorm.build(config, Point(1, 3)),
                CommandoWorm.build(config, Point(2, 3)),
                CommandoWorm.build(config, Point(3, 3))
        )

        val targetPlayer = WormsPlayer(targetWorms)
        val startingPosition = Point(2, 2)
        val attacker = CommandoWorm.build(config, startingPosition)
        val attackingPlayer = WormsPlayer(listOf(attacker))

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 5, 5, CellType.AIR)

        val directions = listOf(UP_LEFT, UP, UP_RIGHT,
                LEFT, RIGHT, DOWN_LEFT, DOWN, DOWN_RIGHT)

        for ((index, direction) in directions.withIndex()) {
            testMap.currentRound++
            val testCommand = DirectionShootCommand(direction)

            assertTrue(testCommand.validate(testMap, attacker).isValid)
            testCommand.execute(testMap, attacker)

            assertEquals(testMap.currentRound, targetWorms[index].hitRound, "Hit round for worm in direction $direction")
            assertEquals(expectedHp, targetWorms[index].health, "Health for worm in direction $direction")
        }

        assertEquals(attacker.health, config.commandoWorms.initialHp)
    }

    @Test
    fun test_apply_validFar() {

        val targetWorms = listOf(
                CommandoWorm.build(config, Point(0, 0)),
                CommandoWorm.build(config, Point(2, 0)),
                CommandoWorm.build(config, Point(4, 0)),
                CommandoWorm.build(config, Point(0, 2)),
                CommandoWorm.build(config, Point(4, 2)),
                CommandoWorm.build(config, Point(0, 4)),
                CommandoWorm.build(config, Point(2, 4)),
                CommandoWorm.build(config, Point(4, 4))
        )

        val targetPlayer = WormsPlayer(targetWorms)
        val startingPosition = Point(2, 2)
        val attacker = CommandoWorm.build(config, startingPosition)
        val attackingPlayer = WormsPlayer(listOf(attacker))

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 5, 5, CellType.AIR)

        val directions = listOf(UP_LEFT, UP, UP_RIGHT,
                LEFT, RIGHT, DOWN_LEFT, DOWN, DOWN_RIGHT)

        for ((index, direction) in directions.withIndex()) {
            testMap.currentRound++
            val testCommand = DirectionShootCommand(direction)

            assertTrue(testCommand.validate(testMap, attacker).isValid)
            testCommand.execute(testMap, attacker)

            assertEquals(testMap.currentRound, targetWorms[index].hitRound, "Hit round for worm in direction $direction")
            assertEquals(expectedHp, targetWorms[index].health, "Health for worm in direction $direction")
        }

        assertEquals(attacker.health, config.commandoWorms.initialHp)
    }

    @Test
    fun test_apply_valid_friendlyFire() {

        val targetWorms = listOf(
                CommandoWorm.build(config, Point(1, 1)),
                CommandoWorm.build(config, Point(2, 1)),
                CommandoWorm.build(config, Point(3, 1)),
                CommandoWorm.build(config, Point(1, 2)),
                CommandoWorm.build(config, Point(3, 2)),
                CommandoWorm.build(config, Point(1, 3)),
                CommandoWorm.build(config, Point(2, 3)),
                CommandoWorm.build(config, Point(3, 3))
        )

        val startingPosition = Point(2, 2)
        val attacker = CommandoWorm.build(config, startingPosition)
        val attackingPlayer = WormsPlayer(targetWorms + attacker)

        val testMap = buildMapWithCellType(listOf(attackingPlayer), 5, 5, CellType.AIR)

        val directions = listOf(UP_LEFT, UP, UP_RIGHT,
                LEFT, RIGHT, DOWN_LEFT, DOWN, DOWN_RIGHT)

        for ((index, direction) in directions.withIndex()) {
            testMap.currentRound++
            val testCommand = DirectionShootCommand(direction)

            testCommand.execute(testMap, attacker)

            assertEquals(testMap.currentRound, targetWorms[index].hitRound, "Hit round for worm in direction $direction")
            assertEquals(expectedHp, targetWorms[index].health, "Health for worm in direction $direction")
        }

        assertEquals(attacker.health, config.commandoWorms.initialHp)
    }

    @Test
    fun test_apply_noObstacles() {

        val startingPosition = Point(2, 2)
        val attacker = CommandoWorm.build(config, startingPosition)
        val attackingPlayer = WormsPlayer(listOf(attacker))

        val testMap = buildMapWithCellType(listOf(attackingPlayer), 5, 5, CellType.AIR)

        val directions = listOf(UP_LEFT, UP, UP_RIGHT,
                LEFT, RIGHT, DOWN_LEFT, DOWN, DOWN_RIGHT)

        for (direction in directions) {
            testMap.currentRound++
            val testCommand = DirectionShootCommand(direction)

            assertTrue(testCommand.validate(testMap, attacker).isValid)
            testCommand.execute(testMap, attacker)
        }

        assertEquals(attacker.health, config.commandoWorms.initialHp)
    }

    @Test
    fun test_apply_obstacles() {

        val targetWorms = listOf(
                CommandoWorm.build(config, Point(0, 0)),
                CommandoWorm.build(config, Point(2, 0)),
                CommandoWorm.build(config, Point(4, 0)),
                CommandoWorm.build(config, Point(0, 2)),
                CommandoWorm.build(config, Point(4, 2)),
                CommandoWorm.build(config, Point(0, 4)),
                CommandoWorm.build(config, Point(2, 4)),
                CommandoWorm.build(config, Point(4, 4))
        )

        val targetPlayer = WormsPlayer(targetWorms)
        val startingPosition = Point(2, 2)
        val attacker = CommandoWorm.build(config, startingPosition)
        val attackingPlayer = WormsPlayer(listOf(attacker))

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 5, 5, CellType.AIR)
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
            val testCommand = DirectionShootCommand(direction)

            assertTrue(testCommand.validate(testMap, attacker).isValid)
            testCommand.execute(testMap, attacker)
        }

        assertEquals(attacker.health, config.commandoWorms.initialHp)
    }


}