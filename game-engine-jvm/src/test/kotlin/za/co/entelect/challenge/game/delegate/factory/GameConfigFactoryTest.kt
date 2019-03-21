package za.co.entelect.challenge.game.delegate.factory

import kotlin.test.Test
import kotlin.test.assertEquals

const val TEST_CONFIG_PATH = "game-engine-jvm/src/main/resources/default-config.json"
val TEST_CONFIG = GameConfigFactory.getConfig(TEST_CONFIG_PATH)

class GameConfigFactoryTest {

    @Test
    fun getConfig() {
        val config = GameConfigFactory.getConfig(TEST_CONFIG_PATH)

        assertEquals(200, config.maxRounds)
        assertEquals(10, config.maxDoNothings)

        assertEquals(3, config.commandoWorms.count)
        assertEquals(100, config.commandoWorms.initialHp)
        assertEquals(1, config.commandoWorms.movementRage)
        assertEquals(1, config.commandoWorms.diggingRange)
        assertEquals(10, config.commandoWorms.weapon.damage)
        assertEquals(3, config.commandoWorms.weapon.range)

        assertEquals(4, config.pushbackDamage)
        assertEquals(32, config.mapSize)
        assertEquals(5, config.healthPackHp)
    }
}
