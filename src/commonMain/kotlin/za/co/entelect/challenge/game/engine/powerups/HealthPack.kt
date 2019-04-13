package za.co.entelect.challenge.game.engine.powerups

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.player.Worm

/**
 * Increases a worm's health when applied
 */
class HealthPack(override val value: Int) : Powerup {

    override val printable = PRINTABLE

    override val type: String = "HEALTH_PACK"

    override fun applyTo(worm: Worm) {
        worm.health += value
    }

    companion object {
        fun build(config: GameConfig): HealthPack {
            return HealthPack(config.healthPackHp)
        }

        const val PRINTABLE = "╠╣"
    }

}
