package za.co.entelect.challenge.game.engine.processor

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.implementation.*
import za.co.entelect.challenge.game.engine.factory.TestMapFactory.buildMapWithCellType
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.powerups.HealthPack
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
    fun  processRound_applyHealthpackNoResurrection() {
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
     * When the engine receives no commands for a specific player it should execute a "do nothing" command
     */
    @Test
    fun  processRound_noCommands() {
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
    fun  processRound_selectAndMove() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0)), CommandoWorm.build(1, config, Point(1, 1))), config)
        val player2 = WormsPlayer.build(2, listOf(CommandoWorm.build(0, config, Point(0, 2))), config)

        val map = buildMapWithCellType(listOf(player1, player2), 5, CellType.AIR)

        roundProcessor.processRound(map, mapOf(player1 to emptyList()))

        assertEquals(1, player1.consecutiveDoNothingsCount)
        assertEquals(1, player2.consecutiveDoNothingsCount)
    }

    /**
     * When the engine receives only a select command for a player, their do nothing count should also be incremented
     */
    @Test
    fun  processRound_selectOnly() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0)), CommandoWorm.build(1, config, Point(1, 1))), config)
        val player2 = WormsPlayer.build(2, listOf(CommandoWorm.build(0, config, Point(0, 2))), config)

        val map = buildMapWithCellType(listOf(player1, player2), 5, CellType.AIR)

        roundProcessor.processRound(map, commandMap(player1, player2, SelectCommand(0)))

        assertEquals(1, player1.consecutiveDoNothingsCount)
        assertEquals(1, player2.consecutiveDoNothingsCount)
    }


    private fun commandMap(player1: WormsPlayer, player2: WormsPlayer, command1: WormsCommand, command2: WormsCommand = command1) =
            mapOf(Pair(player1, listOf(command1)), Pair(player2, listOf(command2)))

    private fun commandMap(player: WormsPlayer, command: WormsCommand) =
            mapOf(Pair(player, listOf(command)))
}
