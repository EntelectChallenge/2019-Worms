package za.co.entelect.challenge.game.engine.command.feedback

class CommandValidation private constructor(
        val isValid: Boolean,
        val isNothing: Boolean,
        val reason: String
) {

    companion object {
        private const val VALID_MOVE = "Valid Move"

        fun invalidMove(reason: String): CommandValidation {
            return CommandValidation(false, true, reason)
        }

        fun validMove(doNothing: Boolean = false, reason: String = VALID_MOVE): CommandValidation {
            return CommandValidation(true, doNothing, reason)
        }
    }
}