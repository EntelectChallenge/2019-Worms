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
class ShootCommand(private val direction: Direction) : WormsCommand {

    override fun validate(gameMap: WormsMap, worm: Worm): MoveValidation {
        return MoveValidation.validMove()
    }

    override fun execute(gameMap: WormsMap, worm: Worm) {
        var position = worm.position + direction.vector

        while (position in gameMap
                && position.shootingDistance(worm.position) <= worm.weapon.range
                && gameMap[position].type.movable) {
            val cell = gameMap[position]

            if (cell.isOccupied()) {
                cell.occupier!!.takeDamage(worm.weapon.damage, gameMap.currentRound)
                break
            }

            position += direction.vector
        }
        //TODO: Return feedback?
        //How will player/visualiser know which worm was hit?
    }

}