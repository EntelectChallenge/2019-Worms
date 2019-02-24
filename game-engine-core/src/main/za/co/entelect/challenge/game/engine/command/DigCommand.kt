package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.entities.MoveValidation
import za.co.entelect.challenge.game.engine.entities.WormsMap
import za.co.entelect.challenge.game.engine.exception.InvalidCommandException
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.Worm

class DigCommand(val target: Point) : WormsCommand {
    constructor(x: Int, y: Int) : this(Point(x, y))

    override fun validate(gameMap: WormsMap, worm: Worm): MoveValidation {
        val targetCell = gameMap[target]

        if (!targetCell.type.diggable) {
            return MoveValidation.invalidMove("Cell type ${targetCell.type} not diggable")
        }

        if (target.movementDistance(worm.position) > worm.diggingRange) {
            return MoveValidation.invalidMove("Cell $target too far away")
        }

        return MoveValidation.validMove()
    }

    override fun execute(gameMap: WormsMap, worm: Worm) {
        val targetCell = gameMap[target]

        val moveValidation = validate(gameMap, worm)
        if (!moveValidation.isValid) {
            throw InvalidCommandException("Invalid Dig Command: ${moveValidation.reason}")
        }

        targetCell.type = CellType.AIR
    }

}