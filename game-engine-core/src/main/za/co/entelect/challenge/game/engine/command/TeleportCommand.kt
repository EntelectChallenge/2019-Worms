package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.entities.MoveValidation
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm
import kotlin.random.Random

class TeleportCommand(val target: Point, val random: Random) : WormsCommand {

    override val order: Int = 2

    constructor(x: Int, y: Int, random: Random) : this(Point(x, y), random)

    override fun validate(gameMap: WormsMap, worm: Worm): MoveValidation {
        if (target !in gameMap) {
            return MoveValidation.invalidMove("$target out of map bounds")
        }

        val targetCell = gameMap[target]

        if (!targetCell.type.open) {
            return MoveValidation.invalidMove("Cannot move to ${targetCell.type}")
        }

        if (target.movementDistance(worm.position) > worm.movementRange) {
            return MoveValidation.invalidMove("Target too far away")
        }

        val occupier = targetCell.occupier
        if (occupier != null && !wormsCollide(gameMap, worm, occupier)) {
            return MoveValidation.invalidMove("Target occupied")
        }

        return MoveValidation.validMove()
    }

    /**
     * Two movements in this turn are colliding.
     */
    private fun wormsCollide(gameMap: WormsMap, movingWorm: Worm, occupier: Worm): Boolean {
        return occupier != movingWorm && occupier.roundMoved == gameMap.currentRound
    }

    override fun execute(gameMap: WormsMap, worm: Worm) {
        val targetCell = gameMap[target]
        val occupier = targetCell.occupier
        if (occupier != null && wormsCollide(gameMap, worm, occupier)) {
            val config = gameMap.config

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