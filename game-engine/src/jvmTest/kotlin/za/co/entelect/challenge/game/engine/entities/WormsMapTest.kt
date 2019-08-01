package za.co.entelect.challenge.game.engine.entities

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.command.implementation.DoNothingCommand
import za.co.entelect.challenge.game.engine.factory.TestMapFactory.buildMapCells
import za.co.entelect.challenge.game.engine.factory.TestMapFactory.buildMapWithCellType
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.processor.WormsRoundProcessor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class WormsMapTest {

    val config = TEST_CONFIG

    @Test(expected = IndexOutOfBoundsException::class)
    fun test_get_xLow() {
        val map = buildMapWithCellType(emptyList(), 2, CellType.AIR)
        map[-1, 0]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun test_get_xHigh() {
        val map = buildMapWithCellType(emptyList(), 2, CellType.AIR)
        map[2, 0]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun test_get_yLow() {
        val map = buildMapWithCellType(emptyList(), 2, CellType.AIR)
        map[0, -1]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun test_get_yHigh() {
        val map = buildMapWithCellType(emptyList(), 2, CellType.AIR)
        map[0, 2]
    }

    @Test
    fun test_getLivingPlayers_one() {
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        worm.health = 0
        val player = WormsPlayer.build(0, listOf(worm), config)

        val worm2 = CommandoWorm.build(0, config, Point(1, 1))
        val player2 = WormsPlayer.build(0, listOf(worm2), config)

        val map = buildMapWithCellType(listOf(player, player2), 2, CellType.AIR)
        assertEquals(1, map.livingPlayers.size)
        assertEquals(player2, map.livingPlayers[0])
        assertEquals(player2, map.winningPlayer)
    }

    @Test
    fun test_getWinningPlayer_commandScore() {
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        val player1 = WormsPlayer.build(0, listOf(worm), config)
        player1.commandScore = 4

        val worm2 = CommandoWorm.build(0, config, Point(1, 1))
        val player2 = WormsPlayer.build(1, listOf(worm2), config)
        player2.commandScore = 10

        val map = buildMapWithCellType(listOf(player1, player2), 2, CellType.AIR)
        assertEquals(2, map.livingPlayers.size)


        val winningPlayer = map.winningPlayer

        assertEquals(player2, winningPlayer)
    }

    @Test
    fun test_getWinningPlayer_healthScore() {
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        val player1 = WormsPlayer.build(0, listOf(worm), config)
        player1.commandScore = 4

        val worm2 = CommandoWorm.build(0, config, Point(1, 1))
        val player2 = WormsPlayer.build(1, listOf(worm2), config)

        worm2.health = 5
        player2.commandScore = 10

        val map = buildMapWithCellType(listOf(player1, player2), 2, CellType.AIR)
        assertEquals(2, map.livingPlayers.size)


        val winningPlayer = map.winningPlayer

        assertEquals(player1, winningPlayer)
    }

    @Test
    fun test_winningPlayer_tie() {
        val worm = CommandoWorm.build(0, config, Point(0, 0))
        val player1 = WormsPlayer.build(0, listOf(worm), config)
        player1.commandScore = 10

        val worm2 = CommandoWorm.build(0, config, Point(1, 1))
        val player2 = WormsPlayer.build(1, listOf(worm2), config)
        player2.commandScore = 10

        val worm3 = CommandoWorm.build(0, config, Point(2, 2))
        val player3 = WormsPlayer.build(1, listOf(worm3), config)

        val map = buildMapWithCellType(listOf(player1, player2, player3), 3, CellType.AIR)

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

        val map = buildMapWithCellType(listOf(player1, player2), 3, CellType.AIR)

        val winningPlayer = map.winningPlayer

        assertEquals(1, map.livingPlayers.size)
        assertEquals(player1, map.livingPlayers[0])
        assertEquals(player1, winningPlayer)
    }

    @Test
    fun test_getLivingPlayers_none() {
        val map = buildMapWithCellType(emptyList(), 2, CellType.AIR)

        assertEquals(0, map.livingPlayers.size)
        assertNull(map.winningPlayer)
    }

    /**
     * Test that no exception gets thrown if a valid coordinate is accessed
     */
    @Test
    fun test_getValid() {
        val map = buildMapWithCellType(emptyList(), 2, CellType.AIR)
        map[0, 0]
        map[0, 1]
        map[1, 0]
        map[1, 1]
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_init_tooManyCells() {
        WormsMap(emptyList(), 2, buildMapCells(5, CellType.AIR))

    }

    @Test(expected = IllegalArgumentException::class)
    fun test_init_tooLittleCells() {
        WormsMap(emptyList(), 2, buildMapCells(3, CellType.AIR))
    }

    @Test
    fun test_removeDeadWorms_dead() {
        val worm1 = CommandoWorm.build(0, config, Point(0, 0))
        val worm2 = CommandoWorm.build(0, config, Point(1, 0))
        val player1 = WormsPlayer.build(0, listOf(worm1, worm2), config)

        worm1.health = 0

        val map = buildMapWithCellType(listOf(player1), 3, CellType.AIR)

        assertEquals(worm1, map[worm1.position].occupier, "Worm 1 placed correctly")
        assertEquals(worm2, map[worm2.position].occupier, "Worm 2 placed correctly")

        map.removeDeadWorms()

        assertFalse(map.cells.any { it.occupier == worm1 }, "Worm 1 removed")
        assertEquals(worm2, map[worm2.position].occupier, "Worm 2 not removed")
    }

    @Test
    fun test_removeDeadWorms_disqualified() {
        val worm1 = CommandoWorm.build(0, config, Point(0, 0))
        val worm2 = CommandoWorm.build(0, config, Point(1, 0))

        val player1 = WormsPlayer.build(0, listOf(worm1, worm2), config)
        player1.consecutiveDoNothingsCount = config.maxDoNothings + 1

        val map = buildMapWithCellType(listOf(player1), 3, CellType.AIR)

        assertEquals(worm1, map[worm1.position].occupier, "Worm 1 placed correctly")
        assertEquals(worm2, map[worm2.position].occupier, "Worm 2 placed correctly")

        map.removeDeadWorms()

        assertFalse(map.cells.any { it.occupier != null }, "Disqualified worms removed")
    }

    /**
     * Living worms that occupy the positions of dead worms should not be removed from the map by removeDeadWorms()
     */
    @Test
    fun test_removeDeadWorms_alreadyRemoved() {
        val worm1 = CommandoWorm.build(0, config, Point(0, 0))
        val worm2 = CommandoWorm.build(0, config, Point(1, 0))
        val player1 = WormsPlayer.build(0, listOf(worm1, worm2), config)

        worm1.health = 0

        val map = buildMapWithCellType(listOf(player1), 3, CellType.AIR)

        assertEquals(worm1, map[worm1.position].occupier, "Worm 1 placed correctly")
        assertEquals(worm2, map[worm2.position].occupier, "Worm 2 placed correctly")

        map.removeDeadWorms()

        assertFalse(map.cells.any { it.occupier == worm1 }, "Worm 1 removed")
        assertEquals(worm2, map[worm2.position].occupier, "Worm 2 not removed")

        worm2.moveTo(map, worm1.position)

        map.removeDeadWorms()

        assertFalse(map.cells.any { it.occupier == worm1 }, "Worm 1 removed")
        assertEquals(worm2, map[worm2.position].occupier, "Worm 2 not removed")
    }

    /**
     * Referee issues are logged and retrievable
     */
    @Test
    fun test_referee_issues_set_and_get() {
        val wormsFor0 = listOf(CommandoWorm.build(0, config, Point(0, 0)))
        val wormsFor1 = listOf(CommandoWorm.build(0, config, Point(1, 0)))
        val player0 = WormsPlayer.build(0, wormsFor0, config)
        val player1 = WormsPlayer.build(1, wormsFor1, config)

        val map = buildMapWithCellType(listOf(player0, player1), 3, CellType.AIR)
        val processor = WormsRoundProcessor(config)

        val doNothingCommand = DoNothingCommand(config)
        val commandMap = mapOf(Pair(player0, listOf(doNothingCommand)), Pair(player1, listOf(doNothingCommand)))

        val refereeIssuesPerRound = mutableListOf<List<String>>()
        repeat(4) {
            processor.processRound(map, commandMap) // processor does: detectRefereeIssues()
            refereeIssuesPerRound.add(map.getRefereeIssues().map { it })

            map.currentRound++
        }

        assertEquals(refereeIssuesPerRound.toString(),
                "[[], " +
                        "[], " +
                        "[DoNothingsCount for @Player(0) reached a count of 3 @Round(2), " +
                        "DoNothingsCount for @Player(1) reached a count of 3 @Round(2)], " +
                        "[DoNothingsCount for @Player(0) reached a count of 3 @Round(2), " +
                        "DoNothingsCount for @Player(1) reached a count of 3 @Round(2)]]",
                "Referee did not output the expected results")

    }
}
