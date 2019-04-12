package za.co.entelect.challenge.game.engine.command.feedback

open class CommandFeedback(val score: Int, val success: Boolean = true) {
    open val message: String = ""
}