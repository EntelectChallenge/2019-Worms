package za.co.entelect.challenge.game.engine.command.implementation

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.command.feedback.BananaResult
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.factory.TestMapFactory.buildMapWithCellType
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.AgentWorm
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BananaCommandTest {

    private val config: GameConfig = TEST_CONFIG

    @Test
    fun test_valid_close() {
        val targetPlayer = WormsPlayer.build(0, listOf(CommandoWorm.build(0, config, Point(1, 1))), config)

        val attacker = AgentWorm.build(0, config, Point(0, 0))
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 15, CellType.AIR)

        val inRangeCoordinate = Point(config.agentWorms.bananas?.range!!, 1)
        val testCommand = BananaCommand(inRangeCoordinate, config)
        val commandValidation = testCommand.validate(testMap, attacker)
        assertTrue(commandValidation.isValid, "Expected a valid banana command")

        val result = testCommand.execute(testMap, attacker)
        assertEquals(result.result, BananaResult.TERRAIN)
        assertEquals(result.target, inRangeCoordinate)
        assertEquals(result.score, 0)
        assertTrue(result.success)
    }

    @Test
    fun test_not_valid_non_agent_worm() {
        val targetPlayer = WormsPlayer.build(0, listOf(CommandoWorm.build(0, config, Point(1, 1))), config)

        val attacker = CommandoWorm.build(0, config, Point(0, 0))
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 15, CellType.AIR)

        val inRangeCoordinate = Point(config.agentWorms.bananas?.range!!, 0)
        val testCommand = BananaCommand(inRangeCoordinate, config)
        val commandValidation = testCommand.validate(testMap, attacker)
        assertFalse(commandValidation.isValid, "Expected an invalid banana command. CommandoWorm cannot throw Bananas!")
    }

    @Test
    fun test_valid_can_throw_over_dirt() {
        val targetPlayer = WormsPlayer.build(0, listOf(CommandoWorm.build(0, config, Point(1, 1))), config)

        val attacker = AgentWorm.build(0, config, Point(0, 0))
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 15, CellType.AIR)
        testMap[1, 0].type = CellType.DIRT

        val overTheWallCoordinate = Point(2, 0)
        val testCommand = BananaCommand(overTheWallCoordinate, config)
        val commandValidation = testCommand.validate(testMap, attacker)
        assertTrue(commandValidation.isValid, "Expected a valid banana command")

        val result = testCommand.execute(testMap, attacker)
        assertEquals(BananaResult.TERRAIN, result.result)
        assertEquals(overTheWallCoordinate, result.target)
        assertEquals(15, result.score)
        assertTrue(result.success)
    }


    @Test
    fun test_validate_out_of_bounds() {
        val targetPlayer = WormsPlayer.build(0, listOf(CommandoWorm.build(0, config, Point(1, 1))), config)

        val attacker = AgentWorm.build(0, config, Point(0, 0))
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 15, CellType.AIR)

        val beyondRangeCoordinate = Point(testMap.size + 1, 0)
        val testCommand = BananaCommand(beyondRangeCoordinate, config)
        assertFalse(testCommand.validate(testMap, attacker).isValid)
    }

    @Test
    fun test_validate_no_bananas_left() {
        val targetPlayer = WormsPlayer.build(0, listOf(CommandoWorm.build(0, config, Point(1, 1))), config)

        val attacker = AgentWorm.build(0, config, Point(0, 0))
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 15, CellType.AIR)

        val inRangeCoordinate = Point(0, 0)
        val testCommand = BananaCommand(inRangeCoordinate, config)

        testCommand.execute(testMap, attacker)
        testCommand.execute(testMap, attacker)
        testCommand.execute(testMap, attacker)

        assertFalse(testCommand.validate(testMap, attacker).isValid)
    }

    @Test
    fun test_validate_too_far() {
        val targetPlayer = WormsPlayer.build(0, listOf(CommandoWorm.build(0, config, Point(1, 1))), config)

        val attacker = AgentWorm.build(0, config, Point(0, 0))
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 15, CellType.AIR)

        val beyondRangeCoordinate = Point(config.agentWorms.bananas?.range!! + 1, 0)
        val testCommand = BananaCommand(beyondRangeCoordinate, config)
        assertFalse(testCommand.validate(testMap, attacker).isValid)
    }

    @Test
    fun test_banana_destroys_dirt() {
        val targetWorm = CommandoWorm.build(0, config, Point(2, 2))
        val targetPlayer = WormsPlayer.build(0, listOf(targetWorm), config)

        val attacker = AgentWorm.build(0, config, Point(0, 0))
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 15, CellType.DIRT)

        val targetCoordinate = Point(3, 3)
        val testCommand = BananaCommand(targetCoordinate, config)
        val result = testCommand.execute(testMap, attacker)

        val visualMap = Point.getAllPointsOfASquare(0, 6).map { testMap[it].type }
                .chunked(7)
                .joinToString(separator = "\n") { line -> line.joinToString(separator = "") { it.printable } }
        assertEquals(visualMap, """
                                |▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                                |▓▓▓▓▓▓░░▓▓▓▓▓▓
                                |▓▓▓▓░░░░░░▓▓▓▓
                                |▓▓░░░░░░░░░░▓▓
                                |▓▓▓▓░░░░░░▓▓▓▓
                                |▓▓▓▓▓▓░░▓▓▓▓▓▓
                                |▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                                """.trimMargin())
        assertEquals(result.score, 113)
    }

    @Test
    fun test_banana_does_splash_damage() {
        val targetWormsLineA = (2..5).map { CommandoWorm.build(0, config, Point(it, 0)) }
        val targetWormsLineB = (2..5).map { CommandoWorm.build(0, config, Point(it, 1)) }
        val allTargetWorms = targetWormsLineA + targetWormsLineB
        val targetPlayer = WormsPlayer.build(0, allTargetWorms, config)

        val attacker = AgentWorm.build(0, config, Point(0, 0))
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 15, CellType.DIRT)

        val targetCoordinate = targetWormsLineA.first().position
        val testCommand = BananaCommand(targetCoordinate, config)
        val result = testCommand.execute(testMap, attacker)
        assertEquals(result.result, BananaResult.BULLSEYE)

        val damages = allTargetWorms.map { Pair(it.position, config.commandoWorms.initialHp - it.health) }

        val listOfExpectedDamages = listOf(
                20, 13, 7, 0,
                13, 11, 0, 0
        )
        assertTrue(damages.zip(listOfExpectedDamages).all { it.first.second == it.second })
    }

}
