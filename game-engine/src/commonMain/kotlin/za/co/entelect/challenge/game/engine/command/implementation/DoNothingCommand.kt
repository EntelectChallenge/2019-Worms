package za.co.entelect.challenge.game.engine.command.implementation

import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.feedback.StandardCommandFeedback
import za.co.entelect.challenge.game.engine.command.feedback.CommandValidation
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm

/**
 * The player decides to do nothing
 */
class DoNothingCommand(val config: GameConfig) : WormsCommand {

    override val order: Int = 0

    override fun validate(gameMap: WormsMap, worm: Worm): CommandValidation {
        return CommandValidation.validMove(true, "Player chose to do nothing")
    }

    override fun execute(gameMap: WormsMap, worm: Worm): StandardCommandFeedback = StandardCommandFeedback(this.toString(), score = config.scores.doNothing, playerId = worm.player.id)

    override fun toString(): String = "nothing"
}
