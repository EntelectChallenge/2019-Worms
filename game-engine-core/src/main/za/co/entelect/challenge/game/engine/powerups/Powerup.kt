package za.co.entelect.challenge.game.engine.powerups

import za.co.entelect.challenge.game.engine.interfaces.Printable
import za.co.entelect.challenge.game.engine.player.Worm

interface Powerup : Printable {

    /**
     * Apply this powerup to a worm
     */
    fun applyTo(worm: Worm)

    val type: String
}
