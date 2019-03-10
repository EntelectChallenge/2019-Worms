package za.co.entelect.challenge.game.engine.entities

import kotlin.test.Test
import za.co.entelect.challenge.game.engine.command.TestMapFactory
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

class WormsMapTest {

    val config = GameConfig()

    @Test(expected = IndexOutOfBoundsException::class)
    fun test_get_xLow() {
        val map = TestMapFactory.buildMapWithCellType(emptyList(), 2, 2, CellType.AIR)
        map[-1, 0]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun test_get_xHigh() {
        val map = TestMapFactory.buildMapWithCellType(emptyList(), 2, 2, CellType.AIR)
        map[2, 0]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun test_get_yLow() {
        val map = TestMapFactory.buildMapWithCellType(emptyList(), 2, 2, CellType.AIR)
        map[0, -1]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun test_get_yHigh() {
        val map = TestMapFactory.buildMapWithCellType(emptyList(), 2, 2, CellType.AIR)
        map[0, 2]
    }

    @Test
    fun test_getLivingPlayers_one() {
        val worm = CommandoWorm.build(config, Point(0, 0))
        worm.health = 0
        val player = WormsPlayer(0, listOf(worm))

        val worm2 = CommandoWorm.build(config, Point(1, 1))
        val player2 = WormsPlayer(0, listOf(worm2))

        val map = TestMapFactory.buildMapWithCellType(listOf(player, player2), 2, 2, CellType.AIR)
        assertEquals(1, map.livingPlayers.size)
        assertEquals(player2, map.livingPlayers[0])
        assertEquals(player2, map.winningPlayer)
    }

    @Test
    fun test_getLivingPlayers_multiple() {
        val worm = CommandoWorm.build(config, Point(0, 0))
        val player = WormsPlayer(0, listOf(worm))

        val worm2 = CommandoWorm.build(config, Point(1, 1))
        val player2 = WormsPlayer(1, listOf(worm2))

        val map = TestMapFactory.buildMapWithCellType(listOf(player, player2), 2, 2, CellType.AIR)
        assertEquals(2, map.livingPlayers.size)
        try {
            map.winningPlayer
            fail("No exception thrown")
        } catch (e: IllegalStateException) {
        }
    }

    @Test
    fun test_getLivingPlayers() {
        val map = TestMapFactory.buildMapWithCellType(emptyList(), 2, 2, CellType.AIR)

        assertEquals(0, map.livingPlayers.size)
        assertNull(map.winningPlayer)
    }

    @Test
    fun test_valid() {
        val map = TestMapFactory.buildMapWithCellType(emptyList(), 2, 2, CellType.AIR)
        map[0, 0]
        map[0, 1]
        map[1, 0]
        map[1, 1]
    }
}