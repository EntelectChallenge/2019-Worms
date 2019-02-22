package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.contracts.exceptions.InvalidCommandException
import za.co.entelect.challenge.game.engine.entities.WormsMap
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.Worm
import kotlin.math.abs

class TeleportCommand(val target: Point): WormsCommand {
    constructor(x : Int, y: Int):  this(Point(x, y))

    override fun isValid(gameMap: WormsMap, player: Worm): Boolean {
        val targetCell = gameMap[target]
        val xDistance = abs(player.position.x - target.x)
        val yDistance = abs(player.position.y - target.y)

        if (!targetCell.type.movable
                || xDistance > player.movementRange
                || yDistance > player.movementRange
                || targetCell.occupied) {
            return false
        }

        return true
    }

    override fun execute(gameMap: WormsMap, worm: Worm) {
        if (!isValid(gameMap, worm)) {
            throw InvalidCommandException("Invalid Move Command")
        }

        worm.moveTo(gameMap, target)
    }

}