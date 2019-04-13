package za.co.entelect.challenge.game.engine.command.feedback

import za.co.entelect.challenge.game.engine.map.Point

class ShootCommandFeedback(command: String, playerId: Int, score: Int, val result: ShootResult, val target: Point) : CommandFeedback(command=command, score=score, playerId=playerId, success=result == ShootResult.HIT) {
    override val message = "Shot $result at (${target.x}, ${target.y})"
}

enum class ShootResult {
    HIT,
    BLOCKED,
    OUT_OF_RANGE
}
