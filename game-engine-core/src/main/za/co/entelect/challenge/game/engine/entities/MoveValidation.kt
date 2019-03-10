package za.co.entelect.challenge.game.engine.entities

class MoveValidation private constructor(
        val isValid: Boolean,
        val reason: String
) {

    companion object {
        private const val VALID_MOVE = "Valid Move"

        fun invalidMove(reason: String): MoveValidation {
            return MoveValidation(false, reason)
        }

        fun validMove(reason: String = VALID_MOVE): MoveValidation {
            return MoveValidation(true, reason)
        }
    }
}