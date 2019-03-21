package za.co.entelect.challenge.game.engine.processor

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.factory.TestMapFactory
import za.co.entelect.challenge.game.engine.command.implementation.*
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.random.Random
import kotlin.test.*

class WormsRoundProcessorTest {

    val config = TEST_CONFIG
    val roundProcessor = WormsRoundProcessor(config)
    val random = Random(0)

    @Test
    fun processRound_digSameHole() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(2, 2))), config)
        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 3, CellType.DIRT)
        val command = DigCommand(1, 1, TEST_CONFIG)

        val commandMap = mapOf(Pair(player1, command), Pair(player2, command))
        roundProcessor.processRound(map, commandMap)

        assertEquals(CellType.AIR, map[1, 1].type)
    }

    @Test
    fun processRound_moveSameLocation() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(2, 2))), config)
        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 3, CellType.AIR)
        val command = TeleportCommand(Point(1, 1), random, TEST_CONFIG)

        val commandMap = mapOf(Pair(player1, command), Pair(player2, command))
        roundProcessor.processRound(map, commandMap)

        assertNull(map[1, 1].occupier)
        assertNotNull(map[0, 0].occupier)
        assertNotNull(map[2, 2].occupier)
    }

    @Test
    fun processRound_moveDigSameLocation() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(2, listOf(CommandoWorm.build(0, config, Point(2, 2))), config)
        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 3, CellType.DIRT)
        val digCommand =  DigCommand(1, 1, TEST_CONFIG)
        val moveCommand = TeleportCommand(Point(1, 1), random, TEST_CONFIG)

        val commandMap = mapOf(Pair(player1, digCommand), Pair(player2, moveCommand))
        roundProcessor.processRound(map, commandMap)

        assertNull(map[1, 1].occupier)
        assertEquals(CellType.AIR, map[1,1].type)
        assertEquals(player1.worms[0], map[0, 0].occupier)
        assertEquals(player2.worms[0], map[2, 2].occupier)
        assertEquals(1, map.currentRoundErrors.size)
    }


    @Test
    fun processRound_shootDigOpen() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(2, 2))), config)
        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 3, CellType.DIRT)
        map[0,0].type = CellType.AIR

        val digCommand =  DigCommand(1, 1, TEST_CONFIG)
        val shootCommand = ShootCommand(Direction.UP_LEFT, TEST_CONFIG)

        val commandMap = mapOf(Pair(player1, digCommand), Pair(player2, shootCommand))
        roundProcessor.processRound(map, commandMap)

        assertNull(map[1, 1].occupier)
        assertEquals(CellType.AIR, map[1,1].type)
        assertEquals(config.commandoWorms.initialHp, player2.worms[0].health)
        assertNotEquals(config.commandoWorms.initialHp, player1.worms[0].health)
    }

    @Test
    fun processRound_moveIntoShot() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(1, 1))), config)
        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 3, CellType.AIR)

        val shootCommand = ShootCommand(Direction.DOWN, TEST_CONFIG)
        val moveCommand = TeleportCommand(Point(0, 2), random, TEST_CONFIG)

        val commandMap = mapOf(Pair(player1, shootCommand), Pair(player2, moveCommand))
        roundProcessor.processRound(map, commandMap)

        assertNotEquals(config.commandoWorms.initialHp, player2.worms[0].health)
        assertEquals(config.commandoWorms.initialHp, player1.worms[0].health)
    }

    @Test
    fun processRound_moveOutOfShot() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 2))), config)
        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 3, CellType.AIR)

        val shootCommand = ShootCommand(Direction.DOWN, TEST_CONFIG)
        val moveCommand = TeleportCommand(Point(1, 1), random, TEST_CONFIG)

        val commandMap = mapOf(Pair(player1, shootCommand), Pair(player2, moveCommand))
        roundProcessor.processRound(map, commandMap)

        assertEquals(config.commandoWorms.initialHp, player2.worms[0].health)
        assertEquals(config.commandoWorms.initialHp, player1.worms[0].health)
    }

    @Test
    fun getPlayerErrors() {
        val player1 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 0))), config)
        val player2 = WormsPlayer.build(1, listOf(CommandoWorm.build(0, config, Point(0, 2))), config)
        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 3, CellType.AIR)

        assertEquals(0, roundProcessor.getErrorList(map).size)

        val invalidCommand = InvalidCommand("Test")
        val commandMap = mapOf(Pair(player1, invalidCommand))

        roundProcessor.processRound(map, commandMap)

        assertEquals(1, roundProcessor.getErrorList(map).size)

        assertEquals(1, roundProcessor.getErrorList(map, player1).size)
        assertEquals(0, roundProcessor.getErrorList(map, player2).size)


        roundProcessor.processRound(map, commandMap)
        //Errors from previous rounds must not be returned
        assertEquals(1, roundProcessor.getErrorList(map).size)
        assertEquals(1, roundProcessor.getErrorList(map, player1).size)
        assertEquals(0, roundProcessor.getErrorList(map, player2).size)
    }
}
