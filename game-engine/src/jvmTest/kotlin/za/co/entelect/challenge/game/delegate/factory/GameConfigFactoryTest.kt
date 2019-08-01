package za.co.entelect.challenge.game.delegate.factory

import kotlin.test.Test
import kotlin.test.assertEquals

const val TEST_CONFIG_PATH = "src/jvmTest/resources/test-config.json"
val TEST_CONFIG = GameConfigFactory.getConfig(TEST_CONFIG_PATH)

class GameConfigFactoryTest {

    @Test
    fun getConfig() {
        val config = GameConfigFactory.getConfig(TEST_CONFIG_PATH)

        assertEquals(400, config.maxRounds)
        assertEquals(12, config.maxDoNothings)

        assertEquals(1, config.commandoWorms.count)
        assertEquals(150, config.commandoWorms.initialHp)
        assertEquals(1, config.commandoWorms.movementRage)
        assertEquals(1, config.commandoWorms.diggingRange)
        assertEquals(8, config.commandoWorms.weapon.damage)
        assertEquals(4, config.commandoWorms.weapon.range)

        assertEquals(1, config.agentWorms.count)
        assertEquals(100, config.agentWorms.initialHp)
        assertEquals(1, config.agentWorms.movementRage)
        assertEquals(1, config.agentWorms.diggingRange)
        assertEquals(8, config.agentWorms.weapon.damage)
        assertEquals(4, config.agentWorms.weapon.range)
        assertEquals(20, config.agentWorms.bananas?.damage)
        assertEquals(5, config.agentWorms.bananas?.range)
        assertEquals(3, config.agentWorms.bananas?.count)
        assertEquals(2, config.agentWorms.bananas?.damageRadius)

        assertEquals(1, config.technologistWorms.count)
        assertEquals(100, config.technologistWorms.initialHp)
        assertEquals(1, config.technologistWorms.movementRage)
        assertEquals(1, config.technologistWorms.diggingRange)
        assertEquals(8, config.technologistWorms.weapon.damage)
        assertEquals(4, config.technologistWorms.weapon.range)
        assertEquals(5, config.technologistWorms.snowballs?.freezeDuration)
        assertEquals(5, config.technologistWorms.snowballs?.range)
        assertEquals(3, config.technologistWorms.snowballs?.count)
        assertEquals(1, config.technologistWorms.snowballs?.freezeRadius)

        assertEquals(20, config.pushbackDamage)
        assertEquals(3, config.lavaDamage)
        assertEquals(33, config.mapSize)
        assertEquals(10, config.healthPackHp)

    }
}
