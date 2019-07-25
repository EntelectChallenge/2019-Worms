package za.co.entelect.challenge.game.engine.renderer.printables

import za.co.entelect.challenge.game.engine.command.CommandStrings
import za.co.entelect.challenge.game.engine.command.feedback.CommandFeedback
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.jvm.Transient

class PrintablePlayer(player: WormsPlayer) {

    val id: Int = player.id
    val score: Int = player.totalScore
    var health: Int? = null
    val currentWormId = player.currentWorm.id
    val remainingWormSelections = player.wormSelectionTokens
    var worms: List<PrintableWorm> = emptyList()
    var previousCommand: String = CommandStrings.NOTHING

    @Transient
    val consoleHealth: Int = player.health

    companion object {
        /**
         * Check if @player is actually the @perspectivePlayer
         */
        fun isPerspectivePlayer(player: WormsPlayer, perspectivePlayer: WormsPlayer?) =
                (player == perspectivePlayer) || (perspectivePlayer == null)

        /**
         * Build a PrintablePlayer from @player that is modified to fit the perspective of @perspectivePlayer
         * @perspectivePlayer is not allowed to see some details from other players
         */
        fun buildForPerspectivePlayer(player: WormsPlayer, perspectivePlayer: WormsPlayer?, wormsMap: WormsMap): PrintablePlayer {
            val playerForPerspectivePlayer = PrintablePlayer(player)
            playerForPerspectivePlayer.worms = player.worms
                    .map { PrintableWorm.buildForDetailsPerspectivePlayer(it, perspectivePlayer) }
            if (isPerspectivePlayer(player, perspectivePlayer)) {
                playerForPerspectivePlayer.health = player.health
            }
            playerForPerspectivePlayer.previousCommand = getLastCommand(wormsMap, player)
            return playerForPerspectivePlayer
        }

        private fun getLastCommand(wormsMap: WormsMap, player: WormsPlayer): String {
            val opponentFeedback = wormsMap
                .getFeedback(wormsMap.currentRound - 1)
                .filter { it.playerId == player.id }
            val feedbackCount = opponentFeedback.size
            return when {
                (feedbackCount == 1) -> opponentFeedback.get(0).command
                (feedbackCount == 2) -> extractSelectCommand(opponentFeedback)
                else                 -> CommandStrings.NOTHING
            }
        }

        private fun extractSelectCommand(opponentFeedback: List<CommandFeedback>): String {
            val selectCommand = opponentFeedback
                .filter { it.command.startsWith("select") }
                .get(0)
            val otherCommand  = opponentFeedback
                .filter { !it.command.startsWith("select") }
                .get(0)
            return "${selectCommand.command}; ${otherCommand.command}"
        }
    }

}
