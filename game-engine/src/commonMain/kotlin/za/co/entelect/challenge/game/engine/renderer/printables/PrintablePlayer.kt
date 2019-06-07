package za.co.entelect.challenge.game.engine.renderer.printables

import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.jvm.Transient

class PrintablePlayer(player: WormsPlayer) {

    val id: Int = player.id
    val score: Int = player.totalScore
    var health: Int? = null
    val currentWormId = player.currentWorm.id
    val remainingWormSelections = player.wormSelectionTokens
    var worms: List<PrintableWorm> = emptyList()

    @Transient
    val consoleHealth: Int = player.health

    companion object {
        /**
         * Check if @player is actually the @perspectivePlayer
         */
        fun isPerspectivePlayer(player: WormsPlayer, perspectivePlayer: WormsPlayer) = player == perspectivePlayer

        /**
         * Build a PrintablePlayer from @player that is modified to fit the perspective of @perspectivePlayer
         * @perspectivePlayer is not allowed to see some details from other players
         */
        fun buildForPerspectivePlayer(player: WormsPlayer, perspectivePlayer: WormsPlayer): PrintablePlayer {
            val playerForPerspectivePlayer = PrintablePlayer(player)
            playerForPerspectivePlayer.worms = player.worms
                    .map { PrintableWorm.buildForDetailsPerspectivePlayer(it, perspectivePlayer) }
            if (isPerspectivePlayer(player, perspectivePlayer)) {
                playerForPerspectivePlayer.health = player.health
            }
            return playerForPerspectivePlayer
        }
    }

}
