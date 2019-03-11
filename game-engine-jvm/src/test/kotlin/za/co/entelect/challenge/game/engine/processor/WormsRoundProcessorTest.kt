package za.co.entelect.challenge.game.engine.processor

import za.co.entelect.challenge.game.engine.command.*
import za.co.entelect.challenge.game.engine.entities.Direction
import za.co.entelect.challenge.game.engine.entities.GameConfig
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.random.Random
import kotlin.test.*

class WormsRoundProcessorTest {

    val config = GameConfig()
    val roundProcessor = WormsRoundProcessor()
    val random = Random(0)

    @Test
    fun processRound_digSameHole() {
        val player1 = WormsPlayer(1, listOf(CommandoWorm.build(config, Point(0, 0))))
        val player2 = WormsPlayer(1, listOf(CommandoWorm.build(config, Point(2, 2))))
        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 3, 3, CellType.DIRT)
        val command = DigCommand(1, 1)

        val commandMap = mapOf(Pair(player1, command), Pair(player2, command))
        roundProcessor.processRound(map, commandMap)

        assertEquals(CellType.AIR, map[1, 1].type)
    }

    @Test
    fun processRound_moveSameLocation() {
        val player1 = WormsPlayer(1, listOf(CommandoWorm.build(config, Point(0, 0))))
        val player2 = WormsPlayer(1, listOf(CommandoWorm.build(config, Point(2, 2))))
        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 3, 3, CellType.AIR)
        val command = TeleportCommand(Point(1, 1), random)

        val commandMap = mapOf(Pair(player1, command), Pair(player2, command))
        roundProcessor.processRound(map, commandMap)

        assertNull(map[1, 1].occupier)
        assertNotNull(map[0, 0].occupier)
        assertNotNull(map[2, 2].occupier)
    }

    @Test
    fun processRound_moveDigSameLocation() {
        val player1 = WormsPlayer(1, listOf(CommandoWorm.build(config, Point(0, 0))))
        val player2 = WormsPlayer(1, listOf(CommandoWorm.build(config, Point(2, 2))))
        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 3, 3, CellType.DIRT)
        val digCommand =  DigCommand(1, 1)
        val moveCommand = TeleportCommand(Point(1, 1), random)

        val commandMap = mapOf(Pair(player1, digCommand), Pair(player2, moveCommand))
        roundProcessor.processRound(map, commandMap)

        assertNull(map[1, 1].occupier)
        assertEquals(CellType.AIR, map[1,1].type)
        assertEquals(player1.worms[0], map[0, 0].occupier)
        assertEquals(player2.worms[0], map[2, 2].occupier)
        assertEquals(1, map.errorList.size)
    }


    @Test
    fun processRound_shootDigOpen() {
        val player1 = WormsPlayer(1, listOf(CommandoWorm.build(config, Point(0, 0))))
        val player2 = WormsPlayer(1, listOf(CommandoWorm.build(config, Point(2, 2))))
        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 3, 3, CellType.DIRT)
        map[0,0].type = CellType.AIR

        val digCommand =  DigCommand(1, 1)
        val shootCommand = ShootCommand(Direction.UP_LEFT)

        val commandMap = mapOf(Pair(player1, digCommand), Pair(player2, shootCommand))
        roundProcessor.processRound(map, commandMap)

        assertNull(map[1, 1].occupier)
        assertEquals(CellType.AIR, map[1,1].type)
        assertEquals(config.commandoWorms.initialHp, player2.worms[0].health)
        assertNotEquals(config.commandoWorms.initialHp, player1.worms[0].health)
    }

    @Test
    fun processRound_moveIntoShot() {
        val player1 = WormsPlayer(1, listOf(CommandoWorm.build(config, Point(0, 0))))
        val player2 = WormsPlayer(1, listOf(CommandoWorm.build(config, Point(1, 1))))
        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 3, 3, CellType.AIR)

        val shootCommand = ShootCommand(Direction.DOWN)
        val moveCommand = TeleportCommand(Point(0, 2), random)

        val commandMap = mapOf(Pair(player1, shootCommand), Pair(player2, moveCommand))
        roundProcessor.processRound(map, commandMap)

        assertNotEquals(config.commandoWorms.initialHp, player2.worms[0].health)
        assertEquals(config.commandoWorms.initialHp, player1.worms[0].health)
    }

    @Test
    fun processRound_moveOutOfShot() {
        val player1 = WormsPlayer(1, listOf(CommandoWorm.build(config, Point(0, 0))))
        val player2 = WormsPlayer(1, listOf(CommandoWorm.build(config, Point(0, 2))))
        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 3, 3, CellType.AIR)

        val shootCommand = ShootCommand(Direction.DOWN)
        val moveCommand = TeleportCommand(Point(1, 1), random)

        val commandMap = mapOf(Pair(player1, shootCommand), Pair(player2, moveCommand))
        roundProcessor.processRound(map, commandMap)

        assertEquals(config.commandoWorms.initialHp, player2.worms[0].health)
        assertEquals(config.commandoWorms.initialHp, player1.worms[0].health)
    }

    @Test
    fun getPlayerErrors() {
        val player1 = WormsPlayer(1, listOf(CommandoWorm.build(config, Point(0, 0))))
        val player2 = WormsPlayer(1, listOf(CommandoWorm.build(config, Point(0, 2))))
        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 3, 3, CellType.AIR)

        assertEquals(0, roundProcessor.getErrorList(map).size)

        val invalidCommand = InvalidCommand("Test")
        val commandMap = mapOf(Pair(player1, invalidCommand))

        roundProcessor.processRound(map, commandMap)

        assertEquals(1, roundProcessor.getErrorList(map).size)

        assertEquals(1, roundProcessor.getErrorList(map, player1).size)
        assertEquals(0, roundProcessor.getErrorList(map, player2).size)
    }
}