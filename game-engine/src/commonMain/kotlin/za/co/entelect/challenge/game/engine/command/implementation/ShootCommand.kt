package za.co.entelect.challenge.game.engine.command.implementation

import mu.KotlinLogging
import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.feedback.CommandValidation
import za.co.entelect.challenge.game.engine.command.feedback.ShootCommandFeedback
import za.co.entelect.challenge.game.engine.command.feedback.ShootResult
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.Point
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

        logger.info { "Starting shoot command: $worm at ${worm.position} shooting ${direction.shortCardinal}" }

        while (position in gameMap
                && position.shootingDistance(worm.position) <= worm.weapon.range) {
            val cell = gameMap[position]

            logger.debug { "Executing shoot command: $worm at $cell" }

            if (!cell.type.open) {
                logger.debug { "Shot blocked: $worm $cell" }
                return ShootCommandFeedback(this.toString(), score = config.scores.missedAttack, playerId = worm.player.id, result = ShootResult.BLOCKED, target = position)
            }

            if (cell.isOccupied()) {
                logger.debug { "Shot hit: $worm shooting $cell ${cell.occupier}" }
                val occupier = cell.occupier!!
                occupier.takeDamage(worm.weapon.damage, gameMap.currentRound)

                return when {
                    occupier.dead -> shootCommandHitFeedback(config.scores.killShot, worm, position)
                    occupier.player == worm.player -> shootCommandHitFeedback(config.scores.friendlyFire, worm, position)
                    else -> shootCommandHitFeedback(config.scores.attack, worm, position)
                }
            }

            position += direction.vector
        }

        logger.debug { "Shot out of range: $worm at $position" }
        return ShootCommandFeedback(this.toString(), score = config.scores.missedAttack, playerId = worm.player.id, result = ShootResult.OUT_OF_RANGE, target = position)
    }

    private fun shootCommandHitFeedback(score: Int, worm: Worm, position: Point) =
            ShootCommandFeedback(this.toString(), score = score, playerId = worm.player.id, result = ShootResult.HIT, target = position)

    override fun toString(): String {
        return "shoot ${direction.shortCardinal}"
    }

    companion object {
        val logger = KotlinLogging.logger { }
    }
}
