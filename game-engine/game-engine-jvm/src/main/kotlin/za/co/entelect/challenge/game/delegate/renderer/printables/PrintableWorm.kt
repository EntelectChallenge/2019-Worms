package za.co.entelect.challenge.game.delegate.renderer.printables

import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.Weapon
import za.co.entelect.challenge.game.engine.player.Worm
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class PrintableWorm private constructor(worm: Worm) {

    var id: Int = worm.id
    var playerId: Int? = null
    var health: Int? = worm.health
    var position: Point? = worm.position
    var weapon: Weapon? = null
    var diggingRange: Int? = worm.diggingRange
    var movementRange: Int? = worm.movementRange
    @Transient
    var printable: String = worm.printable

    companion object {
        /**
         * Build a **game details** header version of a PrintableWorm from @worm that is modified to fit the
         * perspective of @perspectivePlayer
         */
        fun buildForDetailsPerspectivePlayer(worm: Worm, perspectivePlayer: WormsPlayer): PrintableWorm {
            val wormForPerspectivePlayer = PrintableWorm(worm)
            if (PrintablePlayer.isPerspectivePlayer(worm.player, perspectivePlayer)) {
                wormForPerspectivePlayer.weapon = worm.weapon
            }
            return wormForPerspectivePlayer
        }

        /**
         * Build a **map cell** occupier version of a PrintableWorm from @worm that is modified to fit the
         * perspective of @perspectivePlayer
         */
        fun buildForMapPerspectivePlayer(worm: Worm, perspectivePlayer: WormsPlayer): PrintableWorm {
            val wormForPerspectivePlayer = buildForDetailsPerspectivePlayer(worm, perspectivePlayer)
            wormForPerspectivePlayer.playerId = worm.player.id
            return wormForPerspectivePlayer
        }
    }

}
