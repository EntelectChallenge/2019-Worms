package za.co.entelect.challenge.game.engine.entities

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.factory.TestMapFactory
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WormsMapTest {

    val config = TEST_CONFIG

    @Test(expected = IndexOutOfBoundsException::class)
    fun test_get_xLow() {
        val map = TestMapFactory.buildMapWithCellType(emptyList(), 2, CellType.AIR)
        map[-1, 0]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun test_get_xHigh() {
        val map = TestMapFactory.buildMapWithCellType(emptyList(), 2, CellType.AIR)
        map[2, 0]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun test_get_yLow() {
        val map = TestMapFactory.buildMapWithCellType(emptyList(), 2, CellType.AIR)
        map[0, -1]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun test_get_yHigh() {
        val map = TestMapFactory.buildMapWithCellType(emptyList(), 2, CellType.AIR)
        map[0, 2]
    }

    @Test
    fun test_getLivingPlayers_one() {
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        worm.health = 0
        val player = WormsPlayer.build(0, listOf(worm), config)

        val worm2 = CommandoWorm.build(0, config, Point(1, 1))
        val player2 = WormsPlayer.build(0, listOf(worm2), config)

        val map = TestMapFactory.buildMapWithCellType(listOf(player, player2), 2, CellType.AIR)
        assertEquals(1, map.livingPlayers.size)
        assertEquals(player2, map.livingPlayers[0])
        assertEquals(player2, map.winningPlayer)
    }

    @Test
    fun test_getWinningPlayer_score() {
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        val player1 = WormsPlayer.build(0, listOf(worm), config)
        player1.score = 4

        val worm2 = CommandoWorm.build(0, config, Point(1, 1))
        val player2 = WormsPlayer.build(1, listOf(worm2), config)
        player2.score = 10

        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 2, CellType.AIR)
        assertEquals(2, map.livingPlayers.size)


        val winningPlayer = map.winningPlayer

        assertEquals(player2, winningPlayer)
    }

    @Test
    fun test_winningPlayer_tie() {
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        val player1 = WormsPlayer.build(0, listOf(worm), config)
        player1.score = 10

        val worm2 = CommandoWorm.build(0, config, Point(1, 1))
        val player2 = WormsPlayer.build(1, listOf(worm2), config)
        player2.score = 10

        val worm3 = CommandoWorm.build(0, config, Point(2, 2))
        val player3 = WormsPlayer.build(1, listOf(worm3), config)

        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2, player3), 3, CellType.AIR)

        val winningPlayer = map.winningPlayer

        assertNull(winningPlayer)
    }

    @Test
    fun test_disqualifiedWorms() {
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        val player1 = WormsPlayer.build(0, listOf(worm), config)

        val worm2 = CommandoWorm.build(0, config, Point(1, 1))
        val player2 = WormsPlayer.build(1, listOf(worm2), config)
        player2.consecutiveDoNothingsCount = config.maxDoNothings + 1

        val map = TestMapFactory.buildMapWithCellType(listOf(player1, player2), 3, CellType.AIR)

        val winningPlayer = map.winningPlayer

        assertEquals(1, map.livingPlayers.size)
        assertEquals(player1, map.livingPlayers[0])
        assertEquals(player1, winningPlayer)
    }

    @Test
    fun test_getLivingPlayers_none() {
        val map = TestMapFactory.buildMapWithCellType(emptyList(), 2, CellType.AIR)

        assertEquals(0, map.livingPlayers.size)
        assertNull(map.winningPlayer)
    }

    /**
     * Test that no exception gets thrown if a valid coordinate is accessed
     */
    @Test
    fun test_getValid() {
        val map = TestMapFactory.buildMapWithCellType(emptyList(), 2, CellType.AIR)
        map[0, 0]
        map[0, 1]
        map[1, 0]
        map[1, 1]
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_init_tooManyCells() {
        WormsMap(emptyList(), 2, TestMapFactory.buildMapCells(5, CellType.AIR))

    }

    @Test(expected = IllegalArgumentException::class)
    fun test_init_tooLittleCells() {
        WormsMap(emptyList(), 2, TestMapFactory.buildMapCells(3, CellType.AIR))
    }
}
