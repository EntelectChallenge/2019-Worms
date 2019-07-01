package za.co.entelect.challenge.game.engine.command.feedback

import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.Worm
import za.co.entelect.challenge.game.engine.renderer.printables.VisualizerEvent

class ShootCommandFeedback(command: String,
                           worm: Worm,
                           score: Int,
                           val result: ShootResult,
                           val target: Point)
    : CommandFeedback(command = command, score = score, playerId = worm.player.id, success = result == ShootResult.HIT) {
    private val start: Point = worm.position
    override val message = "$worm's shot $result at $target from $start"
    override val visualizerEvent = VisualizerEvent("shoot", result.name, worm, start, target)
}

enum class ShootResult { HIT, BLOCKED, OUT_OF_RANGE }
