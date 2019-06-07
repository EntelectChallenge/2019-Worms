package za.co.entelect.challenge.game.engine.renderer.printables

import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.Bananas
import za.co.entelect.challenge.game.engine.player.Weapon
import za.co.entelect.challenge.game.engine.player.Worm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.jvm.Transient

class PrintableWorm private constructor(worm: Worm) {

    val id: Int = worm.id
    var playerId: Int? = null
        private set

    val health: Int? = worm.health
    val position: Point? = worm.position
    var weapon: Weapon? = null
    var bananaBombs: Bananas? = null

    val diggingRange: Int? = worm.diggingRange
    val movementRange: Int? = worm.movementRange
    val profession: String = worm.profession
    @Transient
    val printable: String = worm.printable

    companion object {
        /**
         * Build a **game details** header version of a PrintableWorm from @worm that is modified to fit the
         * perspective of @perspectivePlayer
         */
        fun buildForDetailsPerspectivePlayer(worm: Worm, perspectivePlayer: WormsPlayer): PrintableWorm {
            val wormForPerspectivePlayer = PrintableWorm(worm)
            if (PrintablePlayer.isPerspectivePlayer(worm.player, perspectivePlayer)) {
                wormForPerspectivePlayer.weapon = worm.weapon
                wormForPerspectivePlayer.bananaBombs = worm.bananas
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
