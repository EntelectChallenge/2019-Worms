package za.co.entelect.challenge.game.engine.command.feedback

import za.co.entelect.challenge.game.engine.renderer.printables.VisualizerEvent

abstract class CommandFeedback(val command: String,
                               val score: Int,
                               val playerId: Int,
                               val success: Boolean = true) {
    abstract val message: String
    abstract val visualizerEvent: VisualizerEvent?

    override fun toString(): String = "CommandFeedback(command=$command,success=$success,message=$message)"
}

class StandardCommandFeedback(command: String,
                              score: Int,
                              playerId: Int,
                              success: Boolean = true,
                              override val message: String = "Success",
                              override val visualizerEvent: VisualizerEvent? = null)
    : CommandFeedback(command, score, playerId, success)
