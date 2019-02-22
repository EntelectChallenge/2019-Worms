package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.contracts.exceptions.InvalidCommandException
import za.co.entelect.challenge.game.engine.entities.WormsMap
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.Worm

class DigCommand(val target: Point) : WormsCommand {
    constructor(x: Int, y: Int) : this(Point(x, y))

    override fun isValid(gameMap: WormsMap, worm: Worm): Boolean {
        val targetCell = gameMap[target]

        if (!targetCell.type.diggable
                || target.movementDistance(worm.position) > worm.diggingRange) {
            return false
        }

        return true
    }


    override fun execute(gameMap: WormsMap, worm: Worm) {
        val targetCell = gameMap[target]

        if (!isValid(gameMap, worm)) {
            throw InvalidCommandException("Invalid Dig Command")
        }

        targetCell.type = CellType.AIR
    }

}