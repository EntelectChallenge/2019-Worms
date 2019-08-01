package za.co.entelect.challenge.game.engine.command.feedback

import za.co.entelect.challenge.game.engine.command.CommandStrings
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.Worm
import za.co.entelect.challenge.game.engine.renderer.printables.VisualizerEvent

class SnowballCommandFeedback(command: String,
                              worm: Worm,
                              score: Int,
                              val result: SnowballResult,
                              val target: Point,
                              affectedCells: List<MapCell>)
    : CommandFeedback(command = command, score = score, playerId = worm.player.id, success = result != SnowballResult.DEEP_SPACE) {
    private val start: Point = worm.position
    override val message = "Snowball hit $result at $target from $start"
    override val visualizerEvent = VisualizerEvent(CommandStrings.SNOWBALL.string, result.name, worm, start, target, affectedCells)
}

enum class SnowballResult { BULLSEYE, TERRAIN, DEEP_SPACE }
