package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.implementation.BananaCommand
import za.co.entelect.challenge.game.engine.command.implementation.Direction
import za.co.entelect.challenge.game.engine.command.implementation.DoNothingCommand
import za.co.entelect.challenge.game.engine.command.implementation.ShootCommand
import za.co.entelect.challenge.game.engine.factory.TestMapFactory
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.AgentWorm
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.processor.WormsRoundProcessor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WormTest {

    val config = TEST_CONFIG

    @Test
    fun test_2_shoot_1_and_both_score() {
        val targetCoordinate = Point(1, 1)
        val victimWorm = CommandoWorm.build(0, config, targetCoordinate)

        val traitorWorm = AgentWorm.build(0, config, Point(4, 1))
        val targetWorms = listOf(victimWorm, traitorWorm)
        val targetPlayer = WormsPlayer.build(0, targetWorms, config)

        val attackerWorm = AgentWorm.build(0, config, Point(4, 2))
        val attackingPlayer = WormsPlayer.build(1, listOf(attackerWorm), config)

        val testMap = TestMapFactory.buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 15, CellType.AIR)

        val testCommand = BananaCommand(victimWorm.position, config)

        victimWorm.health = 1
        val opponentResult = testCommand.execute(testMap, attackerWorm)
        val myPlayerResult = testCommand.execute(testMap, traitorWorm)

        assertEquals(opponentResult.score, 20)
        assertEquals(myPlayerResult.score, -20)
        assertEquals(victimWorm.health, -39)
    }

    @Test
    fun test_2nd_shot_kill_should_give_score_to_both() {
        val targetCoordinate = Point(1, 1)
        val victimWorm = CommandoWorm.build(0, config, targetCoordinate)

        val traitorWorm = AgentWorm.build(0, config, Point(4, 1))
        val targetWorms = listOf(traitorWorm, victimWorm)
        val targetPlayer = WormsPlayer.build(0, targetWorms, config)

        val attackerWorm = AgentWorm.build(0, config, Point(4, 2))
        val attackingPlayer = WormsPlayer.build(1, listOf(attackerWorm), config)

        val testMap = TestMapFactory.buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 15, CellType.AIR)

        victimWorm.health = attackerWorm.weapon.damage + 1
        val testCommand = BananaCommand(victimWorm.position, config)

        val commandMap = mapOf(Pair(targetPlayer, listOf(testCommand)),
                Pair(attackingPlayer, listOf(testCommand)))
        val processor = WormsRoundProcessor(config)
        processor.processRound(testMap, commandMap) // processor does: detectRefereeIssues()

        assertEquals(targetPlayer.commandScore, -60)
        assertEquals(attackingPlayer.commandScore, 60)
        assertEquals(victimWorm.health, -31)
    }

    @Test
    fun test_friendly_fire_killshot_on_after_rounds() {
        val targetCoordinate = Point(1, 1)
        val victimWorm = CommandoWorm.build(0, config, targetCoordinate)

        val traitorWorm = AgentWorm.build(0, config, Point(4, 1))
        val targetWorms = listOf(traitorWorm, victimWorm)
        val targetPlayer = WormsPlayer.build(0, targetWorms, config)

        val attackerWorm = AgentWorm.build(0, config, Point(1, 3))
        val attackingPlayer = WormsPlayer.build(1, listOf(attackerWorm), config)

        val testMap = TestMapFactory.buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 5, CellType.AIR)

        assertEquals(100, victimWorm.health, "Victim $victimWorm should have been unharmed at this point")
        assertEquals(0, targetPlayer.commandScore, "Friendly fire player $targetPlayer should have 0 score at this point")
        assertEquals(0, attackingPlayer.commandScore, "Attacking player $attackingPlayer should have 0 score at this point")
        val processor = WormsRoundProcessor(config)
        processor.processRound(testMap, mapOf(
                Pair(targetPlayer, listOf(ShootCommand(Direction.LEFT, config))),
                Pair(attackingPlayer, listOf(DoNothingCommand(config)))))

        assertEquals(92, victimWorm.health, "Victim $victimWorm should have sustained ${traitorWorm.weapon.damage} damage by now")
        assertEquals(-8, targetPlayer.commandScore, "Friendly fire player $targetPlayer should have -8 score for hitting their own worm")
        assertEquals(0, attackingPlayer.commandScore, "Attacking player $attackingPlayer should have 0 score at this point, since they did nothing")

        victimWorm.health = 1
        processor.processRound(testMap, mapOf(
                Pair(targetPlayer, listOf(DoNothingCommand(config))),
                Pair(attackingPlayer, listOf(ShootCommand(Direction.UP, config)))))
        assertTrue(victimWorm.dead, "Victim $victimWorm be dead")
        // This is where friendly fire player used to get penalised for past mistakes, when they certainly did not kill their worm
        assertEquals(-8, targetPlayer.commandScore, "Friendly fire player $targetPlayer should have -8 score for hitting their own worm")
        assertEquals(40 + 8, attackingPlayer.commandScore, "Attacking player $attackingPlayer should have 48 score at this point, since they did nothing")
    }

}
