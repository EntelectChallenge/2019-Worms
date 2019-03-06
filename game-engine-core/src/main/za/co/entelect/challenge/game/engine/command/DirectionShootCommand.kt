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
class DirectionShootCommand(private val direction: Direction) : WormsCommand {

    override fun validate(gameMap: WormsMap, worm: Worm): MoveValidation {
        return MoveValidation.validMove()
    }

    override fun execute(gameMap: WormsMap, worm: Worm) {
        var distance = 1
        var position = worm.position + direction.vector
        var cell = gameMap[position]
        while (distance < worm.weapon.range && cell.type.movable) {
            if (cell.isOccupied()) {
                cell.occupier!!.takeDamage(worm.weapon.damage, gameMap.currentRound)
                //TODO: Return feedback
                break;
            }

            position += direction.vector
            distance += 1

            if (position !in gameMap) {
                //Out of range
                break
            }
            cell = gameMap[position]
        }
        //TODO: Maybe return feedback?
        //How will player/visualiser know which worm was hit?
    }

}