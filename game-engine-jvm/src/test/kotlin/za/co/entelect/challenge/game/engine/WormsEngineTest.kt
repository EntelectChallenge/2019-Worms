package za.co.entelect.challenge.game.engine

import com.nhaarman.mockitokotlin2.mock
import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.map.GameMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WormsEngineTest {

    val config = TEST_CONFIG
    val engine = WormsEngine(config)

    @Test
    fun test_gameEnds_roundCount() {
        val map = mock<GameMap> {
            on { livingPlayers }.thenReturn(listOf(WormsPlayer.build(0, config), WormsPlayer.build(1, config)))
            on { currentRound }.thenReturn(config.maxRounds)
        }

        assertTrue(engine.isGameComplete(map))
    }

    @Test
    fun test_gameEnds_roundCountAndLivingPlayers() {
        val map = mock<GameMap> {
            on { livingPlayers }.thenReturn(listOf(WormsPlayer.build(0, config)))
            on { currentRound }.thenReturn(config.maxRounds)
        }

        assertTrue(engine.isGameComplete(map))
    }

    @Test
    fun test_gameEnds_oneLivingPlayer() {
        val map = mock<GameMap> {
            on { livingPlayers }.thenReturn(listOf(WormsPlayer.build(0, config)))
            on { currentRound }.thenReturn(1)
        }

        assertTrue(engine.isGameComplete(map))
    }

    @Test
    fun test_gameEnds_noLivingPlayer() {
        val map = mock<GameMap> {
            on { livingPlayers }.thenReturn(emptyList())
            on { currentRound }.thenReturn(1)
        }

        assertTrue(engine.isGameComplete(map))
    }

    @Test
    fun test_gameNotEnding() {
        val map = mock<GameMap> {
            on { livingPlayers }.thenReturn(listOf(WormsPlayer.build(0, config), WormsPlayer.build(1, config)))
            on { currentRound }.thenReturn(config.maxRounds - 1)
        }

        assertFalse(engine.isGameComplete(map))
    }
}