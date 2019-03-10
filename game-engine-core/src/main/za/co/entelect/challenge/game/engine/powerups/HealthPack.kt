package za.co.entelect.challenge.game.engine.powerups

import za.co.entelect.challenge.game.engine.entities.GameConfig
import za.co.entelect.challenge.game.engine.player.Worm

/**
 * Increases a worm's health when applied
 */
class HealthPack(private val health: Int) : Powerup {

    override fun applyTo(worm: Worm) {
        worm.health += health
    }

    companion object {
        fun build(config: GameConfig): HealthPack {
            return HealthPack(config.healthPackHp)
        }
    }
}