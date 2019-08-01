package za.co.entelect.challenge.game.engine.renderer.printables

import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.*
import kotlin.jvm.Transient

class PrintableWorm private constructor(worm: Worm) {

    val id: Int = worm.id
    var playerId: Int? = null
        private set

    var health: Int? = worm.health
    var position: Point? = Point(worm.position.x, worm.position.y)
    var weapon: Weapon? = null
    var bananaBombs: Bananas? = null
    var snowballs: Snowballs? = null

    var diggingRange: Int? = worm.diggingRange
    var movementRange: Int? = worm.movementRange
    var roundsUntilUnfrozen: Int? = worm.roundsUntilUnfrozen
    var profession: String? = worm.profession
    @Transient
    val printable: String = worm.printable

    companion object {
        /**
         * Build a **game details** header version of a PrintableWorm from @worm that is modified to fit the
         * perspective of @perspectivePlayer
         */
        fun buildForDetailsPerspectivePlayer(worm: Worm, perspectivePlayer: WormsPlayer?): PrintableWorm {
            val wormForPerspectivePlayer = PrintableWorm(worm)
            if (PrintablePlayer.isPerspectivePlayer(worm.player, perspectivePlayer)) {
                wormForPerspectivePlayer.weapon = worm.weapon
                wormForPerspectivePlayer.bananaBombs = worm.bananas
                wormForPerspectivePlayer.snowballs = worm.snowballs
            }
            return wormForPerspectivePlayer
        }

        /**
         * Build a **map cell** occupier version of a PrintableWorm from @worm that is modified to fit the
         * perspective of @perspectivePlayer
         */
        fun buildForMapPerspectivePlayer(worm: Worm, perspectivePlayer: WormsPlayer?): PrintableWorm {
            val wormForPerspectivePlayer = buildForDetailsPerspectivePlayer(worm, perspectivePlayer)
            wormForPerspectivePlayer.playerId = worm.player.id
            return wormForPerspectivePlayer
        }

        /**
         * Build a PrintableWorm with only worm/player ids
         */
        fun buildForVisualizerEvent(worm: Worm): PrintableWorm {
            val result = PrintableWorm(worm)
            result.playerId = worm.player.id
            result.health = null
            result.position = null
            result.weapon = null
            result.bananaBombs = null
            result.diggingRange = null
            result.movementRange = null
            result.roundsUntilUnfrozen = null
            result.profession = null

            return result
        }
    }

}
