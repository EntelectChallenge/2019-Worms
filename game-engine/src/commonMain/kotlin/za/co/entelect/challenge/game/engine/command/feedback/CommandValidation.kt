package za.co.entelect.challenge.game.engine.command.feedback

data class CommandValidation constructor(val isValid: Boolean,
                                         val isNothing: Boolean,
                                         val reason: String) {

    companion object {
        private const val VALID_MOVE = "Valid Move"

        fun invalidMove(reason: String): CommandValidation = CommandValidation(false, true, reason)

        fun validMove(doNothing: Boolean = false, reason: String = VALID_MOVE): CommandValidation =
                CommandValidation(true, doNothing, reason)
    }
}
