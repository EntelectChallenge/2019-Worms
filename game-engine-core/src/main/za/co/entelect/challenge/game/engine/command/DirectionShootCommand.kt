package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.entities.Direction
import za.co.entelect.challenge.game.engine.entities.MoveValidation
import za.co.entelect.challenge.game.engine.entities.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm

/**
 * - Only vertical, diagonal, horizontal shots are allowed
 * - The shot hits the first occupied cell in the specified direction
 * - Any non-moveable cells block the shot
 */
class DirectionShootCommand(val direction: Direction) : WormsCommand {

    override fun validate(gameMap: WormsMap, worm: Worm): MoveValidation {
        return MoveValidation.validMove()
    }

    override fun execute(gameMap: WormsMap, worm: Worm) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}