package za.co.entelect.challenge.game.engine.powerups

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthPackTest {

    val config = TEST_CONFIG

    @Test
    fun apply() {
        val worm = CommandoWorm.build(0, config)
        val healthPack = HealthPack.build(config)

        healthPack.applyTo(worm)

        assertEquals(config.commandoWorms.initialHp + config.healthPackHp, worm.health)
    }
}