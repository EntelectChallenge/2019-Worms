package za.co.entelect.challenge.game.engine.powerups

import za.co.entelect.challenge.game.engine.player.Worm

interface Powerup {

    /**
     * Apply this powerup to a worm
     */
    fun applyTo(worm: Worm)

    val type: String
}