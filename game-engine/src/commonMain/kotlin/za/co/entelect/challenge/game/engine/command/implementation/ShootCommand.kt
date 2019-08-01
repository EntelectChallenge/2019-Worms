package za.co.entelect.challenge.game.engine.command.implementation

import mu.KotlinLogging
import za.co.entelect.challenge.game.engine.command.CommandStrings
import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.feedback.CommandValidation
import za.co.entelect.challenge.game.engine.command.feedback.ShootCommandFeedback
import za.co.entelect.challenge.game.engine.command.feedback.ShootResult
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm

/**
 * - Only vertical, diagonal, horizontal shots are allowed
 * - The shot hits the first occupied cell in the specified direction
 * - Any non-open cells block the shot
 */
class ShootCommand(val direction: Direction, val config: GameConfig) : WormsCommand {

    override val order: Int = 4

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

            when {
                !cell.type.open -> return shotBlocked(worm, cell, position)
                cell.isOccupied() -> return shotHitWorm(worm, cell, gameMap, position)
                else -> position += direction.vector
            }
        }

        logger.debug { "Shot out of range: $worm at $position" }
        return buildBasicShootCommandFeedback(worm, config.scores.missedAttack, ShootResult.OUT_OF_RANGE, position)
    }

    private fun buildBasicShootCommandFeedback(worm: Worm, score: Int, result: ShootResult, target: Point): ShootCommandFeedback
            = ShootCommandFeedback(toString(), worm, score, result, target)

    private fun shotHitWorm(worm: Worm,
                            cell: MapCell,
                            gameMap: WormsMap,
                            position: Point): ShootCommandFeedback {
        logger.debug { "Shot hit: $worm shooting $cell ${cell.occupier}" }
        val occupier = cell.occupier!!
        val damageScore = config.scores.attack * occupier.takeDamage(worm.weapon.damage, gameMap.currentRound, worm.player)

        val isAllyWorm = occupier.player == worm.player
        return when {
            isAllyWorm -> buildBasicShootCommandFeedback(worm, -damageScore, ShootResult.HIT, position)
            else -> buildBasicShootCommandFeedback(worm, damageScore, ShootResult.HIT, position)
        }
    }

    private fun shotBlocked(worm: Worm,
                            cell: MapCell,
                            position: Point): ShootCommandFeedback {
        logger.debug { "Shot blocked: $worm $cell" }
        return buildBasicShootCommandFeedback(worm, config.scores.missedAttack, ShootResult.BLOCKED, position)
    }

    override fun toString(): String = "${CommandStrings.SHOOT.string} ${direction.shortCardinal}"

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
