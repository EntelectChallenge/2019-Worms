package za.co.entelect.challenge.game.engine.command

import org.junit.Test
import za.co.entelect.challenge.game.engine.command.TestMapFactory.buildMapCells
import za.co.entelect.challenge.game.engine.entities.Direction.*
import za.co.entelect.challenge.game.engine.entities.GameConfig
import za.co.entelect.challenge.game.engine.entities.WormsMap
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.assertEquals


class DirectionShootCommandTest {

    private val config: GameConfig = GameConfig()
    private val expectedHp = config.commandoWorms.initialHp - config.commandoWorms.weapon.damage

    //TODO: Test
    // - valid shot
    // - friendly fire
    // - blocked by dirt
    // - out of range

    @Test
    fun test_apply_valid() {

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

        val testMap = WormsMap(listOf(attackingPlayer, targetPlayer), 5, 5, buildMapCells(25, CellType.AIR))

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

        val testMap = WormsMap(listOf(attackingPlayer), 5, 5, buildMapCells(25, CellType.AIR))

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


}