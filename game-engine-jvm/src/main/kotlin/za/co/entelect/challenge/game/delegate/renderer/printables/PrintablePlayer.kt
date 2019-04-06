package za.co.entelect.challenge.game.delegate.renderer.printables

import za.co.entelect.challenge.game.engine.player.WormsPlayer

class PrintablePlayer(player: WormsPlayer) {

    var id: Int = player.id
    var score: Int = player.totalScore
    var health: Int? = null
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
