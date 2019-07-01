package za.co.entelect.challenge.game.engine.command.feedback

import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.Worm
import za.co.entelect.challenge.game.engine.renderer.printables.VisualizerEvent

class BananaCommandFeedback(command: String,
                            worm: Worm,
                            score: Int,
                            val result: BananaResult,
                            val target: Point)
    : CommandFeedback(command = command, score = score, playerId = worm.player.id, success = result != BananaResult.DEEP_SPACE) {
    private val start: Point = worm.position
    override val message = "Banana hit $result at $target from $start"
    override val visualizerEvent = VisualizerEvent("banana", result.name, worm, start, target)
}

enum class BananaResult { BULLSEYE, TERRAIN, DEEP_SPACE }
