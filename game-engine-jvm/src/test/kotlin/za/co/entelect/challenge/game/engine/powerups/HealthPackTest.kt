package za.co.entelect.challenge.game.engine.powerups

import org.junit.Test

import za.co.entelect.challenge.game.engine.entities.GameConfig
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import kotlin.test.assertEquals

class HealthPackTest {

    val config = GameConfig()

    @Test
    fun apply() {
        val worm = CommandoWorm.build(config)
        val healthPack = HealthPack.build(config)

        healthPack.applyTo(worm)

        assertEquals(config.commandoWorms.initialHp + config.healthPackHp, worm.health)
    }
}