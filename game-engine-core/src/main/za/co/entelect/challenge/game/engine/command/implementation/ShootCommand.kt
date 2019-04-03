package za.co.entelect.challenge.game.engine.command.implementation

import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.feedback.CommandValidation
import za.co.entelect.challenge.game.engine.command.feedback.ShootCommandFeedback
import za.co.entelect.challenge.game.engine.command.feedback.ShootResult
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm

/**
 * - Only vertical, diagonal, horizontal shots are allowed
 * - The shot hits the first occupied cell in the specified direction
 * - Any non-open cells block the shot
 */
class ShootCommand(val direction: Direction, val config: GameConfig) : WormsCommand {

    override val order: Int = 3

    override fun validate(gameMap: WormsMap, worm: Worm): CommandValidation {
        return CommandValidation.validMove()
    }

    override fun execute(gameMap: WormsMap, worm: Worm): ShootCommandFeedback {
        var position = worm.position + direction.vector

        while (position in gameMap
                && position.shootingDistance(worm.position) <= worm.weapon.range) {
            val cell = gameMap[position]

            if (!cell.type.open) {
                return ShootCommandFeedback(config.scores.missedAttack, ShootResult.BLOCKED, position)
            }

            if (cell.isOccupied()) {
                val occupier = cell.occupier!!
                occupier.takeDamage(worm.weapon.damage, gameMap.currentRound)

                return if (occupier.dead) {
                    ShootCommandFeedback(config.scores.killShot, ShootResult.HIT, position)
                } else {
                    ShootCommandFeedback(config.scores.attack, ShootResult.HIT, position)
                }
            }

            position += direction.vector
        }

        return ShootCommandFeedback(config.scores.missedAttack, ShootResult.OUT_OF_RANGE, position)
    }

}