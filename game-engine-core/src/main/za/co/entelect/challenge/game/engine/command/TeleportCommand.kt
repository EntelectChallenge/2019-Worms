package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.contracts.exceptions.InvalidCommandException
import za.co.entelect.challenge.game.engine.entities.MoveValidation
import za.co.entelect.challenge.game.engine.entities.WormsMap
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.Worm

class TeleportCommand(val target: Point) : WormsCommand {
    constructor(x: Int, y: Int) : this(Point(x, y))

    override fun validate(gameMap: WormsMap, worm: Worm): MoveValidation {
        val targetCell = gameMap[target]

        if (!targetCell.type.movable) {
            return MoveValidation.invalidMove("Cannot move to ${targetCell.type}")
        }

        if (target.movementDistance(worm.position) > worm.movementRange) {
            return MoveValidation.invalidMove( "Target too far away")
        }

        val occupier = targetCell.occupier
        if (occupier != null && !wormsCollide(gameMap, worm, occupier)) {
            return MoveValidation.invalidMove( "Target occupied")
        }

        return MoveValidation.validMove()
    }

    /**
     * Two movements in this turn is colliding.
     */
    private fun wormsCollide(gameMap: WormsMap, movingWorm: Worm, occupier: Worm): Boolean {
        return occupier != movingWorm && occupier.roundMoved == gameMap.currentRound
    }

    override fun execute(gameMap: WormsMap, worm: Worm) {
        val targetCell = gameMap[target]

        val moveValidation = validate(gameMap, worm)
        if (!moveValidation.isValid) {
            throw InvalidCommandException("Invalid Move Command: ${moveValidation.reason}")
        }

        val occupier = targetCell.occupier
        if (occupier != null && wormsCollide(gameMap, worm, occupier)) {
            val config = gameMap.config

            worm.takeDamage(config.pushbackDamage)
            occupier.takeDamage(config.pushbackDamage)

            // 50% chance to pushback or swap positions
            if (config.random.nextBoolean()) {
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