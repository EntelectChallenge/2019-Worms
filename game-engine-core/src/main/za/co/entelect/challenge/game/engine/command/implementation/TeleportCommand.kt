package za.co.entelect.challenge.game.engine.command.implementation

import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.feedback.CommandFeedback
import za.co.entelect.challenge.game.engine.command.feedback.CommandValidation
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm
import kotlin.random.Random

class TeleportCommand(val target: Point, val random: Random, val config: GameConfig) : WormsCommand {

    override val order: Int = 2

    constructor(x: Int, y: Int, random: Random, config: GameConfig) : this(Point(x, y), random, config)

    override fun validate(gameMap: WormsMap, worm: Worm): CommandValidation {
        if (target !in gameMap) {
            return CommandValidation.invalidMove("$target out of map bounds")
        }

        val targetCell = gameMap[target]

        if (!targetCell.type.open) {
            return CommandValidation.invalidMove("Cannot move to ${targetCell.type}")
        }

        if (target.movementDistance(worm.position) > worm.movementRange) {
            return CommandValidation.invalidMove("Target too far away")
        }

        val occupier = targetCell.occupier
        if (occupier != null && !wormsCollide(gameMap, worm, occupier)) {
            return CommandValidation.invalidMove("Target occupied")
        }

        return CommandValidation.validMove()
    }

    /**
     * Two movements in this turn are colliding.
     */
    private fun wormsCollide(gameMap: WormsMap, movingWorm: Worm, occupier: Worm): Boolean {
        return occupier != movingWorm && occupier.roundMoved == gameMap.currentRound
    }

    override fun execute(gameMap: WormsMap, worm: Worm): CommandFeedback {
        val targetCell = gameMap[target]
        val occupier = targetCell.occupier
        if (occupier != null && wormsCollide(gameMap, worm, occupier)) {
            worm.takeDamage(config.pushbackDamage, gameMap.currentRound)
            occupier.takeDamage(config.pushbackDamage, gameMap.currentRound)

            // 50% chance to pushback or swap positions
            if (random.nextBoolean()) {
                pushbackWorms(worm, occupier, gameMap)
            } else {
                swapWorms(worm, occupier, gameMap)
            }
        } else {
            worm.moveTo(gameMap, target)
        }

        return CommandFeedback(config.scores.move)
    }

    private fun pushbackWorms(worm: Worm, occupier: Worm, gameMap: WormsMap) {
        val wormPosition = worm.position
        val occupierPosition = occupier.previousPosition

        worm.moveTo(gameMap, wormPosition)
        occupier.moveTo(gameMap, occupierPosition)
    }

    private fun swapWorms(worm: Worm, occupier: Worm, gameMap: WormsMap) {
        val wormPosition = worm.position
        val occupierPosition = occupier.previousPosition

        worm.moveTo(gameMap, occupierPosition)
        occupier.moveTo(gameMap, wormPosition)
    }

}