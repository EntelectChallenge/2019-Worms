package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.entities.MoveValidation
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm

/**
 * The player decides to do nothing
 */
class DoNothingCommand : WormsCommand {
    override val order: Int = 0

    override fun validate(gameMap: WormsMap, worm: Worm): MoveValidation {
        return MoveValidation.validMove()
    }

    override fun execute(gameMap: WormsMap, worm: Worm) {
        worm.player.doNothingsCount++
    }
}