package za.co.entelect.challenge.game.engine.command.implementation

import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.feedback.CommandValidation
import za.co.entelect.challenge.game.engine.command.feedback.TeleportCommandFeedback
import za.co.entelect.challenge.game.engine.command.feedback.TeleportResult
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm
import kotlin.random.Random

class TeleportCommand(val target: Point, private val random: Random, val config: GameConfig) : WormsCommand {

    override val order: Int = 1

    constructor(x: Int, y: Int, random: Random, config: GameConfig) : this(Point(x, y), random, config)

    override fun validate(gameMap: WormsMap, worm: Worm): CommandValidation {
        if (target !in gameMap) return CommandValidation.invalidMove("$target out of map bounds")

        val targetCell = gameMap[target]
        val occupier = targetCell.occupier
        return when {
            target.movementDistance(worm.position) > worm.movementRange -> CommandValidation.invalidMove("Target too far away")
            !targetCell.type.open -> CommandValidation.invalidMove("Cannot move to ${targetCell.type}")
            occupier != null && !wormsCollide(gameMap, worm, occupier) -> CommandValidation.invalidMove("Target occupied")
            else -> CommandValidation.validMove()
        }
    }

    /**
     * Two movements in this turn are colliding.
     */
    private fun wormsCollide(gameMap: WormsMap, movingWorm: Worm, occupier: Worm): Boolean {
        return occupier != movingWorm && occupier.roundMoved == gameMap.currentRound
    }

    override fun execute(gameMap: WormsMap, worm: Worm): TeleportCommandFeedback {
        val targetCell = gameMap[target]
        val occupier = targetCell.occupier
        if (occupier != null && wormsCollide(gameMap, worm, occupier)) {
            worm.takeDamage(config.pushbackDamage, gameMap.currentRound)
            occupier.takeDamage(config.pushbackDamage, gameMap.currentRound)

            return when {
                // 50% chance to pushback or swap positions
                random.nextBoolean() -> pushbackWorms(worm, occupier, gameMap)
                else -> swapWorms(worm, occupier, gameMap)
            }
        } else {
            worm.moveTo(gameMap, target)
            return TeleportCommandFeedback(toString(), worm, config.scores.move, TeleportResult.MOVED, worm.previousPosition, target)
        }
    }

    private fun pushbackWorms(worm: Worm, occupier: Worm, gameMap: WormsMap): TeleportCommandFeedback {
        val wormPosition = worm.position
        val occupierPosition = occupier.previousPosition

        worm.moveTo(gameMap, wormPosition)
        occupier.moveTo(gameMap, occupierPosition)
        return TeleportCommandFeedback(toString(), worm, config.scores.move, TeleportResult.PUSHEDBACK, worm.previousPosition, wormPosition)
    }

    private fun swapWorms(worm: Worm, occupier: Worm, gameMap: WormsMap): TeleportCommandFeedback {
        val wormPosition = worm.position
        val occupierPosition = occupier.previousPosition

        worm.moveTo(gameMap, occupierPosition)
        occupier.moveTo(gameMap, wormPosition)
        return TeleportCommandFeedback(toString(), worm, config.scores.move, TeleportResult.SWAPPED, worm.previousPosition, wormPosition)
    }

    override fun toString(): String = "move $target"

}
