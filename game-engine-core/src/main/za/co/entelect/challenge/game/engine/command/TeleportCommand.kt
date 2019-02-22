package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.contracts.exceptions.InvalidCommandException
import za.co.entelect.challenge.game.engine.entities.WormsMap
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.Worm
import kotlin.math.abs

class TeleportCommand(val target: Point) : WormsCommand {
    constructor(x: Int, y: Int) : this(Point(x, y))

    override fun isValid(gameMap: WormsMap, worm: Worm): Boolean {
        val targetCell = gameMap[target]
        val xDistance = abs(worm.position.x - target.x)
        val yDistance = abs(worm.position.y - target.y)

        if (!targetCell.type.movable
                || xDistance > worm.movementRange
                || yDistance > worm.movementRange
                || targetCell.isOccupied() && !shouldPushback(gameMap, worm, targetCell.occupier)) {
            return false
        }

        return true
    }

    private fun shouldPushback(gameMap: WormsMap, movingWorm: Worm, occupier: Worm?): Boolean {
        return occupier != null && occupier != movingWorm && occupier.roundMoved == gameMap.currentRound
    }

    override fun execute(gameMap: WormsMap, worm: Worm) {
        val targetCell = gameMap[target]

        if (!isValid(gameMap, worm)) {
            throw InvalidCommandException("Invalid Move Command")
        }

        if (shouldPushback(gameMap, worm, targetCell.occupier)) {
            val config = gameMap.config
            val occupier = targetCell.occupier!!

            worm.takeDamage(config.pushbackDamage)
            occupier.takeDamage(config.pushbackDamage)

            val wormPosition = worm.position
            val occupierPosition = occupier.previousPosition

            // 50% chance to pushback or swap positions
            if (config.random.nextBoolean()) {
                worm.moveTo(gameMap, occupierPosition)
                occupier.moveTo(gameMap, wormPosition)
            } else {
                worm.moveTo(gameMap, wormPosition)
                occupier.moveTo(gameMap, occupierPosition)
            }
        } else {
            worm.moveTo(gameMap, target)
        }
    }

}