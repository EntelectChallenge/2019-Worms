package za.co.entelect.challenge.game.engine.command.feedback

import za.co.entelect.challenge.game.engine.map.Point

class ShootCommandFeedback(score: Int, val result: ShootResult, val target: Point) : CommandFeedback(score) {

}

enum class ShootResult {
    HIT,
    BLOCKED,
    OUT_OF_RANGE
}