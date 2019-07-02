package za.co.entelect.challenge.game.engine.command.feedback

import za.co.entelect.challenge.game.engine.command.CommandStrings
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.Worm
import za.co.entelect.challenge.game.engine.renderer.printables.VisualizerEvent

class DigCommandFeedback(command: String,
                         worm: Worm,
                         score: Int,
                         end: Point)
    : CommandFeedback(command = command, score = score, playerId = worm.player.id, success = true) {
    override val message = "Worm dug out $end"
    override val visualizerEvent = VisualizerEvent(CommandStrings.DIG.string, null, worm, null, end, null)
}
