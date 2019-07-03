package za.co.entelect.challenge.game.engine.processor

import org.junit.Assert
import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.implementation.*
import za.co.entelect.challenge.game.engine.factory.TestMapFactory
import za.co.entelect.challenge.game.engine.factory.TestMapFactory.buildMapWithCellType
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.AgentWorm
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.powerups.HealthPack
import za.co.entelect.challenge.game.engine.renderer.WormsRendererText
import kotlin.random.Random
import kotlin.test.*

class WormsRoundProcessorTest {

    private val config = TEST_CONFIG
    private val roundProcessor = WormsRoundProcessor(config)
    private val random = Random(0)

    @Test
    fun processRound_digSameHole() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(2, listOf(CommandoWorm.build(0, config, Point(2, 2))), config)
        val map = buildMapWithCellType(listOf(player1, player2), 3, CellType.DIRT)
        val command = DigCommand(1, 1, TEST_CONFIG)

        val commandMap = commandMap(player1, player2, command)
        roundProcessor.processRound(map, commandMap)

        assertEquals(CellType.AIR, map[1, 1].type)

        assertEquals(player1.commandScore, player2.commandScore)
    }

    @Test
    fun processRound_moveSameLocation() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(2, listOf(CommandoWorm.build(0, config, Point(2, 2))), config)
        val map = buildMapWithCellType(listOf(player1, player2), 3, CellType.AIR)
        val command: WormsCommand = TeleportCommand(Point(1, 1), random, TEST_CONFIG)

        val commandMap = commandMap(player1, player2, command)
        roundProcessor.processRound(map, commandMap)

        assertNull(map[1, 1].occupier)
        assertNotNull(map[0, 0].occupier)
        assertNotNull(map[2, 2].occupier)

        val expectedHealth = config.commandoWorms.initialHp - config.pushbackDamage
        assertEquals(expectedHealth, player1.health)
        assertEquals(expectedHealth, player2.health)
    }

    @Test
    fun processRound_moveSameLocationWithPowerup() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(2, listOf(CommandoWorm.build(0, config, Point(2, 2))), config)
        val map = buildMapWithCellType(listOf(player1, player2), 3, CellType.AIR)
        map[1, 1].powerup = HealthPack(config.healthPackHp)
        val command = TeleportCommand(Point(1, 1), random, TEST_CONFIG)

        val commandMap = commandMap(player1, player2, command)
        roundProcessor.processRound(map, commandMap)

        assertNull(map[1, 1].occupier)
        assertNotNull(map[0, 0].occupier)
        assertNotNull(map[2, 2].occupier)

        val expectedHealth = config.commandoWorms.initialHp - config.pushbackDamage
        assertEquals(player2.health, player1.health, "Players should have equal health")
        assertEquals(expectedHealth, player1.health)
        assertEquals(expectedHealth, player2.health)
    }

    @Test
    fun processRound_moveDigSameLocation() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(2, listOf(CommandoWorm.build(0, config, Point(2, 2))), config)
        val map = buildMapWithCellType(listOf(player1, player2), 3, CellType.DIRT)
        val digCommand = DigCommand(1, 1, TEST_CONFIG)
        val moveCommand = TeleportCommand(Point(1, 1), random, TEST_CONFIG)

        val commandMap = commandMap(player1, player2, digCommand, moveCommand)
        roundProcessor.processRound(map, commandMap)

        assertNull(map[1, 1].occupier)
        assertEquals(CellType.AIR, map[1, 1].type)
        assertEquals(player1.worms[0], map[0, 0].occupier)
        assertEquals(player2.worms[0], map[2, 2].occupier)
        assertEquals(1, map.currentRoundErrors.size)
    }


    @Test
    fun processRound_shootDigOpen() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(2, listOf(CommandoWorm.build(0, config, Point(2, 2))), config)
        val map = buildMapWithCellType(listOf(player1, player2), 3, CellType.DIRT)
        map[0, 0].type = CellType.AIR

        val digCommand = DigCommand(1, 1, TEST_CONFIG)
        val shootCommand = ShootCommand(Direction.UP_LEFT, TEST_CONFIG)

        val commandMap = commandMap(player1, player2, digCommand, shootCommand)
        roundProcessor.processRound(map, commandMap)

        assertNull(map[1, 1].occupier)
        assertEquals(CellType.AIR, map[1, 1].type)
        assertEquals(config.commandoWorms.initialHp, player2.worms[0].health)
        assertNotEquals(config.commandoWorms.initialHp, player1.worms[0].health)
    }

    @Test
    fun processRound_moveIntoShot() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(2, listOf(CommandoWorm.build(0, config, Point(1, 1))), config)
        val map = buildMapWithCellType(listOf(player1, player2), 3, CellType.AIR)

        val shootCommand = ShootCommand(Direction.DOWN, TEST_CONFIG)
        val moveCommand = TeleportCommand(Point(0, 2), random, TEST_CONFIG)

        val commandMap = commandMap(player1, player2, shootCommand, moveCommand)

        roundProcessor.processRound(map, commandMap)

        assertNotEquals(config.commandoWorms.initialHp, player2.worms[0].health)
        assertEquals(config.commandoWorms.initialHp, player1.worms[0].health)
    }

    @Test
    fun processRound_moveOutOfShot() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(2, listOf(CommandoWorm.build(0, config, Point(0, 2))), config)
        val map = buildMapWithCellType(listOf(player1, player2), 3, CellType.AIR)

        val shootCommand = ShootCommand(Direction.DOWN, TEST_CONFIG)
        val moveCommand = TeleportCommand(Point(1, 1), random, TEST_CONFIG)

        val commandMap = commandMap(player1, player2, shootCommand, moveCommand)
        roundProcessor.processRound(map, commandMap)

        assertEquals(config.commandoWorms.initialHp, player2.worms[0].health)
        assertEquals(config.commandoWorms.initialHp, player1.worms[0].health)
    }

    @Test
    fun processRound_deadWormsRemoved() {
        val attackingWorm = CommandoWorm.build(0, config, Point(0, 0))
        val targetWorm = CommandoWorm.build(0, config, Point(0, 2))

        val player1 = WormsPlayer.build(1, listOf(attackingWorm), config)
        val player2 = WormsPlayer.build(2, listOf(targetWorm), config)

        player2.currentWorm.health = attackingWorm.weapon.damage

        val map = buildMapWithCellType(listOf(player1, player2), 3, CellType.AIR)

        assertNotNull(map[attackingWorm.position].occupier)
        assertNotNull(map[targetWorm.position].occupier)

        assertFalse(attackingWorm.dead)
        assertFalse(targetWorm.dead)

        val shootCommand = ShootCommand(Direction.DOWN, TEST_CONFIG)
        val doNothingCommand = DoNothingCommand(config)

        val commandMap = commandMap(player1, player2, shootCommand, doNothingCommand)
        roundProcessor.processRound(map, commandMap)

        assertTrue(targetWorm.dead)
        assertFalse(attackingWorm.dead)

        assertNull(map[targetWorm.position].occupier, "Dead worm has been removed")
        assertNotNull(map[attackingWorm.position].occupier, "Living worm has not been removed")
    }

    @Test
    fun getPlayerErrors() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(2, listOf(CommandoWorm.build(0, config, Point(0, 2))), config)
        val map = buildMapWithCellType(listOf(player1, player2), 3, CellType.AIR)

        assertEquals(0, roundProcessor.getErrorList(map).size)

        val invalidCommand = InvalidCommand("Test")
        val commandMap = commandMap(player1, invalidCommand)

        //The game runner is responsible for updating the round number
        map.currentRound = 1
        roundProcessor.processRound(map, commandMap)

        assertEquals(1, roundProcessor.getErrorList(map).size)

        assertEquals(1, roundProcessor.getErrorList(map, player1).size)
        assertEquals(0, roundProcessor.getErrorList(map, player2).size)

        //The game runner is responsible for updating the round number
        map.currentRound = 2
        roundProcessor.processRound(map, commandMap)

        //Errors from previous rounds must not be returned
        assertEquals(1, roundProcessor.getErrorList(map).size)
        assertEquals(1, roundProcessor.getErrorList(map, player1).size)
        assertEquals(0, roundProcessor.getErrorList(map, player2).size)
    }

    @Test
    fun processRound_applyHealthpack() {
        val worm = CommandoWorm.build(0, config, Point(2, 2))
        val player = WormsPlayer.build(0, listOf(worm), config)
        val map = buildMapWithCellType(listOf(player), 5, CellType.AIR)

        val target = Point(1, 2)
        val command: WormsCommand = TeleportCommand(target, Random, config)
        map[target].powerup = HealthPack(config.healthPackHp)

        command.execute(map, worm)
        roundProcessor.processRound(map, commandMap(player, command))

        val expectedHealth = config.commandoWorms.initialHp + config.healthPackHp
        assertEquals(expectedHealth, player.health)
    }


    /**
     * A healthpack can resurrect a worm that is still on the map (i.e. it died in this round)
     */
    @Test
    fun processRound_applyHealthpackResurrection() {
        val worm = CommandoWorm.build(0, config, Point(2, 2))
        worm.health = 0
        val player = WormsPlayer.build(0, listOf(worm), config)
        val map = buildMapWithCellType(listOf(player), 5, CellType.AIR)

        val target = Point(1, 2)
        val command = TeleportCommand(target, Random, config)
        map[target].powerup = HealthPack(config.healthPackHp)

        command.execute(map, worm)
        roundProcessor.processRound(map, mapOf(Pair(player, listOf(command))))

        assertEquals(config.healthPackHp, player.health)
        assertEquals(worm, map[target].occupier)
        assertNull(map[target].powerup)
        assertFalse(player.dead)
    }

    /**
     * A healthpack cannot resurrect a worm that is no longer on the map (i.e. it died in a previous round)
     */
    @Test
    fun processRound_applyHealthpackNoResurrection() {
        val worm = CommandoWorm.build(0, config, Point(2, 2))
        worm.health = 0
        val player = WormsPlayer.build(0, listOf(worm), config)
        val map = buildMapWithCellType(listOf(player), 5, CellType.AIR)

        val target = Point(1, 2)
        map[target].powerup = HealthPack(config.healthPackHp)

        roundProcessor.processRound(map, emptyMap())

        assertEquals(0, player.health)
        assertNull(map[2, 2].occupier)
        assertNotNull(map[target].powerup)
        assertTrue(player.dead)
    }

    /**
     * If a dead worm (that is no longer on the map) has the same last position as a living worm's current position, the
     * healthpack should only be applied to living worms
     */
    @Test
    fun processRound_applyHealthpackDeadWormPosition() {
        val originPosition = Point(2, 2)
        val targetPosition = Point(1, 1)

        val livingWorm = CommandoWorm.build(0, config, originPosition)
        val deadWorm = CommandoWorm.build(1, config, targetPosition).apply {
            health = 0
        }

        val player = WormsPlayer.build(0, listOf(livingWorm, deadWorm), config)
        val map = buildMapWithCellType(listOf(player), 3, CellType.AIR)

        map.removeDeadWorms()
        assertNull(map[targetPosition].occupier)
        assertFalse(map[targetPosition].isOccupied())

        map[targetPosition].powerup = HealthPack(config.healthPackHp)

        roundProcessor.processRound(map, commandMap(player, TeleportCommand(targetPosition, random, config)))

        assertNull(map[originPosition].occupier)
        assertNotNull(map[targetPosition].occupier)
        assertNull(map[targetPosition].powerup)

        assertTrue(deadWorm.dead)
        assertEquals(0, deadWorm.health)

        assertFalse(livingWorm.dead)
        assertEquals(config.commandoWorms.initialHp + config.healthPackHp, livingWorm.health)
    }

    /**
     * When the engine receives no commands for a specific player it should execute a "do nothing" command
     */
    @Test
    fun processRound_noCommands() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(2, listOf(CommandoWorm.build(0, config, Point(0, 2))), config)

        val map = buildMapWithCellType(listOf(player1, player2), 5, CellType.AIR)

        roundProcessor.processRound(map, mapOf(player1 to emptyList()))

        assertEquals(1, player1.consecutiveDoNothingsCount)
        assertEquals(1, player2.consecutiveDoNothingsCount)
    }

    /**
     * When the engine receives no commands for a specific player it should execute a "do nothing" command
     */
    @Test
    fun processRound_selectAndMove() {
        val player = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0)), CommandoWorm.build(1, config, Point(1, 1))), config)

        assertEquals(player.worms[0], player.currentWorm)

        val map = buildMapWithCellType(listOf(player), 5, CellType.AIR)
        val selectCommand = SelectCommand(1)
        val moveCommand = TeleportCommand(Point(2, 2), random, config)

        roundProcessor.processRound(map, commandMap(player, moveCommand, selectCommand))

        assertEquals(0, player.consecutiveDoNothingsCount)
        assertEquals(player.worms[0], player.currentWorm)
        assertEquals(Point(2, 2), player.worms[1].position)
    }

    @Test
    fun test_shootCommand_sameKill() {
        val traitorWorm = CommandoWorm.build(1, config, Point(0, 0))
        val victimWorm = CommandoWorm.build(1, config, Point(1, 1))

        val targetPlayer = WormsPlayer.build(0, listOf(traitorWorm, victimWorm), config)

        val attackerWorm = CommandoWorm.build(0, config, Point(2, 2))
        val attackingPlayer = WormsPlayer.build(1, listOf(attackerWorm), config)

        val testMap = TestMapFactory.buildMapWithCellType(listOf(attackingPlayer, targetPlayer), 3, CellType.AIR)

        val traitorCommand = ShootCommand(Direction.DOWN_RIGHT, config)
        val otherCommand = ShootCommand(Direction.UP_LEFT, config)

        val commandMap = commandMap(targetPlayer, attackingPlayer, traitorCommand, otherCommand)

        victimWorm.health = 2
        roundProcessor.processRound(testMap, commandMap)

        assertTrue(victimWorm.dead)
        assertEquals(0, victimWorm.health)
        assertEquals(41, attackingPlayer.commandScore)
        assertEquals(-41, targetPlayer.commandScore)

    }

    /**
     * When the engine receives only a select command for a player, their do nothing count should also be incremented
     */
    @Test
    fun processRound_selectOnly() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0)), CommandoWorm.build(1, config, Point(1, 1))), config)
        val player2 = WormsPlayer.build(2, listOf(CommandoWorm.build(0, config, Point(0, 2))), config)

        val map = buildMapWithCellType(listOf(player1, player2), 5, CellType.AIR)

        roundProcessor.processRound(map, commandMap(player1, player2, SelectCommand(0)))

        assertEquals(1, player1.consecutiveDoNothingsCount)
        assertEquals(1, player2.consecutiveDoNothingsCount)
    }

    @Test
    fun processRound_banana() {
        val player1Worms = listOf(AgentWorm.build(1, config, Point(0, 0)), CommandoWorm.build(2, config, Point(1, 1)))
        val player1 = WormsPlayer.build(1, player1Worms, config)

        val player2Worms = listOf(AgentWorm.build(1, config, Point(0, 2)), CommandoWorm.build(2, config, Point(2, 2)))
        val player2 = WormsPlayer.build(2, player2Worms, config)

        val map = buildMapWithCellType(listOf(player1, player2), 5, CellType.AIR)

        roundProcessor.processRound(map, commandMap(player1, player2, BananaCommand(Point(1, 1), config), DoNothingCommand(config)))

        assertEquals(2, player1.worms[0].bananas?.count)
        assertEquals(3, player2.worms[0].bananas?.count)

        val textRenderer = WormsRendererText(config)
        val line1 = textRenderer.render(map, player1)
        val line2 = textRenderer.render(map, player2)

        assertTextRenderedBananaCount(line1, 2)
        assertTextRenderedBananaCount(line2, 3)
    }

    private fun assertTextRenderedBananaCount(line1: String, expected: Int) {
        val pattern = "Banana bombs count: (\\d)".toRegex()

        val matchResult = pattern.findAll(line1).toList()

        Assert.assertEquals(1, matchResult.size)

        val (count) = matchResult[0].destructured
        assertEquals(expected, count.toInt())
    }

    @Test
    fun test_banana_score_destroyed_dirt() {
        val aliceWorm = AgentWorm.build(0, config, Point(3, 2))
        val alice = WormsPlayer.build(0, listOf(aliceWorm), config)

        val bobWorm = AgentWorm.build(0, config, Point(2, 2))
        val bob = WormsPlayer.build(1, listOf(bobWorm), config)

        val map = buildMapWithCellType(listOf(bob, alice), 10, CellType.DIRT)
        roundProcessor.processRound(map, commandMap(bob, alice,
                        BananaCommand(Point(6, 3), config),
                        DoNothingCommand(config)))

        assertEquals(52, bob.commandScore, "Expected to gain 52 score from banana destroyed dirt cells")

        roundProcessor.processRound(map, commandMap(bob, alice,
                BananaCommand(Point(2, 6), config),
                BananaCommand(Point(2, 6), config)))

        assertEquals(104, bob.commandScore, "Expected to gain 104 score from banana destroyed dirt cells")
        assertEquals(52, alice.commandScore, "Expected to gain 52 score from banana destroyed dirt cells")
    }

    private fun commandMap(player1: WormsPlayer, player2: WormsPlayer, command1: WormsCommand, command2: WormsCommand = command1) =
            mapOf(Pair(player1, listOf(command1)), Pair(player2, listOf(command2)))

    private fun commandMap(player: WormsPlayer, vararg command: WormsCommand) =
            mapOf(Pair(player, command.asList()))
}
