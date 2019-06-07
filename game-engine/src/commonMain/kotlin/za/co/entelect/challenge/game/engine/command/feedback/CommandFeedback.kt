package za.co.entelect.challenge.game.engine.command.feedback

import za.co.entelect.challenge.game.engine.map.Point

abstract class CommandFeedback(val command: String, val score: Int, val playerId: Int, val success: Boolean = true) {
    abstract val message: String

    override fun toString(): String {
        return "CommandFeedback(command=$command,success=$success,message=$message)"
    }
}

class StandardCommandFeedback(command: String, score: Int, playerId: Int, success: Boolean = true, override val message: String = "Success") : CommandFeedback(command, score, playerId, success)

class ShootCommandFeedback(command: String, playerId: Int, score: Int, val result: ShootResult, val target: Point) : CommandFeedback(command = command, score = score, playerId = playerId, success = result == ShootResult.HIT) {
    override val message = "Shot $result at (${target.x}, ${target.y})"
}

enum class ShootResult {
    HIT,
    BLOCKED,
    OUT_OF_RANGE
}

class BananaCommandFeedback(command: String, playerId: Int, score: Int, val result: BananaResult, val target: Point)
    : CommandFeedback(command = command, score = score, playerId = playerId, success = true) {
    override val message = "Banana hit $result at $target"
}

enum class BananaResult {
    BULLSEYE,
    TERRAIN,
    DEEP_SPACE
}
