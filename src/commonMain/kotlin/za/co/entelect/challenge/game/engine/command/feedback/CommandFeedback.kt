package za.co.entelect.challenge.game.engine.command.feedback

open class CommandFeedback(val command: String, val score: Int, val playerId: Int, val success: Boolean = true) {
    open val message: String = ""
}
