package za.co.entelect.challenge.game.engine.powerups

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.player.Worm
import kotlin.jvm.Transient

/**
 * Increases a worm's health when applied
 */
class HealthPack(private val health: Int) : Powerup {

    @Transient override val printable = PRINTABLE
    override val type: String = "healthpack"

    override val printable = PRINTABLE

    override fun applyTo(worm: Worm) {
        worm.health += health
    }

    companion object {
        fun build(config: GameConfig): HealthPack {
            return HealthPack(config.healthPackHp)
        }

        const val PRINTABLE = "╠╣"
    }

}
