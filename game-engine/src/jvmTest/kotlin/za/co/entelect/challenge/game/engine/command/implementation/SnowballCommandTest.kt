package za.co.entelect.challenge.game.engine.command.implementation

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.command.feedback.SnowballResult
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.factory.TestMapFactory.buildMapWithCellType
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.TechnologistWorm
import za.co.entelect.challenge.game.engine.player.Worm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SnowballCommandTest {

    private val config: GameConfig = TEST_CONFIG

    @Test
    fun test_valid_close() {
        val (attacker, _, testMap) = getBasicPlayersSetup(6)

        val inRangeCoordinate = Point(config.technologistWorms.snowballs?.range!!, 1)
        val testCommand = SnowballCommand(inRangeCoordinate, config)
        val commandValidation = testCommand.validate(testMap, attacker)
        assertTrue(commandValidation.isValid, "Expected a valid snowball command")

        val result = testCommand.execute(testMap, attacker)
        assertEquals(result.result, SnowballResult.TERRAIN)
        assertEquals(result.target, inRangeCoordinate)
        assertEquals(result.score, 0)
        assertTrue(result.success)
    }

    @Test
    fun test_not_valid_non_technologist_worm() {
        val (_, _, testMap) = getBasicPlayersSetup(6)
        val attacker = CommandoWorm.build(0, config, Point(0, 0))

        val inRangeCoordinate = Point(config.technologistWorms.snowballs?.range!!, 0)
        val testCommand = SnowballCommand(inRangeCoordinate, config)
        val commandValidation = testCommand.validate(testMap, attacker)
        assertFalse(commandValidation.isValid, "Expected an invalid snowball command. CommandoWorm cannot throw Snowballs!")
    }

    @Test
    fun test_valid_can_throw_over_dirt() {
        val (attacker, _, testMap) = getBasicPlayersSetup(5)
        testMap[1, 0].type = CellType.DIRT

        val overTheWallCoordinate = Point(4, 0)
        val testCommand = SnowballCommand(overTheWallCoordinate, config)
        val commandValidation = testCommand.validate(testMap, attacker)
        assertTrue(commandValidation.isValid, "Expected a valid snowball command")

        val result = testCommand.execute(testMap, attacker)
        assertEquals(SnowballResult.TERRAIN, result.result)
        assertEquals(overTheWallCoordinate, result.target)
        assertEquals(0, result.score)
        assertTrue(result.success)
    }

    @Test
    fun test_validate_out_of_bounds() {
        val (attacker, _, testMap) = getBasicPlayersSetup(2)

        val beyondRangeCoordinate = Point(testMap.size + 1, 0)
        val testCommand = SnowballCommand(beyondRangeCoordinate, config)
        assertFalse(testCommand.validate(testMap, attacker).isValid)
    }

    @Test
    fun test_validate_no_snowballs_left() {
        val (attacker, _, testMap) = getBasicPlayersSetup(2)

        val inRangeCoordinate = Point(0, 0)
        val testCommand = SnowballCommand(inRangeCoordinate, config)

        testCommand.execute(testMap, attacker)
        testCommand.execute(testMap, attacker)
        testCommand.execute(testMap, attacker)

        assertFalse(testCommand.validate(testMap, attacker).isValid)
    }

    @Test
    fun test_validate_too_far() {
        val (attacker, _, testMap) = getBasicPlayersSetup(8)

        val beyondRangeCoordinate = Point(config.technologistWorms.snowballs?.range!! + 1, 0)
        val testCommand = SnowballCommand(beyondRangeCoordinate, config)
        assertFalse(testCommand.validate(testMap, attacker).isValid)
    }

    @Test
    fun test_being_frozen_does_not_let_go_of_commands() {
        val (alice, bob, testMap) = getBasicPlayersSetup(3)
        bob.moveTo(testMap, Point(2, 0))

        val aliceCommand = SnowballCommand(bob.position, config)
        val bobCommand = SnowballCommand(alice.position, config)

        aliceCommand.execute(testMap, alice)
        bobCommand.execute(testMap, bob)

        val freezeDuration = config.technologistWorms.snowballs!!.freezeDuration
        assertEquals(freezeDuration, alice.roundsUntilUnfrozen)
        assertEquals(freezeDuration, bob.roundsUntilUnfrozen)
    }

    private fun getBasicPlayersSetup(mapSize: Int): Triple<Worm, Worm, WormsMap> {
        val target = TechnologistWorm.build(0, config, Point(1, 1))
        val targetPlayer = WormsPlayer.build(0, listOf(target), config)

        val attacker = TechnologistWorm.build(0, config, Point(0, 0))
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), mapSize, CellType.AIR)
        return Triple(attacker, target, testMap)
    }

    @Test
    fun test_snowball_does_not_destroy_dirt() {
        val targetWorm = CommandoWorm.build(0, config, Point(1, 1))
        val targetPlayer = WormsPlayer.build(0, listOf(targetWorm), config)

        val attacker = TechnologistWorm.build(0, config, Point(0, 0))
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 15, CellType.DIRT)

        val targetCoordinate = Point(3, 3)
        val testCommand = SnowballCommand(targetCoordinate, config)
        val result = testCommand.execute(testMap, attacker)

        val visualMap = Point.getAllPointsOfASquare(0, 6).map { testMap[it].type }
                .chunked(7)
                .joinToString(separator = "\n") { line -> line.joinToString(separator = "") { it.printable } }
        assertEquals(visualMap, """
                                |▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                                |▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                                |▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                                |▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                                |▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                                |▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                                |▓▓▓▓▓▓▓▓▓▓▓▓▓▓
                                """.trimMargin())
        assertEquals(0, result.score)
    }

    @Test
    fun test_snowball_does_splash_freeze() {
        val targetWormsLineA = (2..5).map { CommandoWorm.build(0, config, Point(it, 0)) }
        val targetWormsLineB = (2..5).map { CommandoWorm.build(0, config, Point(it, 1)) }
        val allTargetWorms = targetWormsLineA + targetWormsLineB
        val targetPlayer = WormsPlayer.build(0, allTargetWorms, config)

        val attacker = TechnologistWorm.build(0, config, Point(0, 0))
        val attackingPlayer = WormsPlayer.build(1, listOf(attacker), config)

        val testMap = buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 15, CellType.DIRT)

        val targetCoordinate = targetWormsLineA.first().position
        val testCommand = SnowballCommand(targetCoordinate, config)
        val result = testCommand.execute(testMap, attacker)
        assertEquals(result.result, SnowballResult.BULLSEYE)

        val durations = allTargetWorms.map { Pair(it.position, it.roundsUntilUnfrozen) }

        val listOfExpectedDamages = listOf(
                5, 5, 0, 0,
                5, 5, 0, 0)
        assertTrue(durations.zip(listOfExpectedDamages).all { (pair, expectedDuration) -> pair.second == expectedDuration })
    }

}
