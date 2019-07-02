package za.co.entelect.challenge.game.engine.command.feedback

import za.co.entelect.challenge.game.engine.command.CommandStrings
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.Worm
import za.co.entelect.challenge.game.engine.renderer.printables.VisualizerEvent

class TeleportCommandFeedback(command: String,
                              worm: Worm,
                              score: Int,
                              result: TeleportResult,
                              start: Point,
                              end: Point)
    : CommandFeedback(command = command, score = score, playerId = worm.player.id, success = result == TeleportResult.MOVED) {
    override val message = "Worm $result from $start to $end"
    override val visualizerEvent = VisualizerEvent(CommandStrings.MOVE.string, result.name, worm, start, end, null)
}

enum class TeleportResult { MOVED, SWAPPED, PUSHEDBACK }
