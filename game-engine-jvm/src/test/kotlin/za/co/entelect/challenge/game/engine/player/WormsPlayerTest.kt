package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.engine.entities.GameConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WormsPlayerTest {

    val config = GameConfig()

    @Test
    fun test_playerBuild() {
        val player = WormsPlayer.build(0, config)

        assertEquals(config.commandoWorms.count, player.worms.size)
        assertEquals(config.commandoWorms.count * config.commandoWorms.initialHp, player.health)
        assertFalse(player.dead)
        assertEquals(player.worms[0], player.currentWorm)

        player.selectNextWorm()

        assertEquals(player.worms[1], player.currentWorm)
    }

    @Test
    fun test_player_wormSelection() {
        val player = WormsPlayer.build(0, config)
        player.worms[1].health = 0

        assertEquals(config.commandoWorms.count, player.worms.size)
        assertEquals((config.commandoWorms.count - 1) * config.commandoWorms.initialHp, player.health)
        assertFalse(player.dead)

        player.selectNextWorm()
        assertEquals(player.worms[2], player.currentWorm)

        player.selectNextWorm()
        assertEquals(player.worms[0], player.currentWorm)
    }

    @Test
    fun test_player_dead() {
        val player = WormsPlayer.build(0, config)
        player.worms.forEachIndexed { i, worm -> worm.health = -i}

        assertEquals(config.commandoWorms.count, player.worms.size)
        assertEquals(0, player.health)
        assertTrue(player.dead)

        assertEquals(player.worms[0], player.currentWorm)
        player.selectNextWorm()
        assertEquals(player.worms[0], player.currentWorm)
    }

}