package za.co.entelect.challenge.game.engine.command.implementation

import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.feedback.StandardCommandFeedback
import za.co.entelect.challenge.game.engine.command.feedback.CommandValidation
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm

/**
 * Command to dig through a cell
 */
class DigCommand(val target: Point, val config: GameConfig) : WormsCommand {

    override val order: Int = 2

    constructor(x: Int, y: Int, config: GameConfig) : this(Point(x, y), config)

    /**
     * For a dig command to be valid:
     * * The target cell must be within range
     * * The target cell must be diggable (see CellType})
     */
    override fun validate(gameMap: WormsMap, worm: Worm): CommandValidation {
        if (target !in gameMap) {
            return CommandValidation.invalidMove("$target out of map bounds")
        }

        val targetCell = gameMap[target]

        if (!targetCell.type.diggable) {
            return CommandValidation.invalidMove("Cell type ${targetCell.type} not diggable")
        }

        if (target.movementDistance(worm.position) > worm.diggingRange) {
            return CommandValidation.invalidMove("Cell $target too far away")
        }

        return CommandValidation.validMove()
    }

    override fun execute(gameMap: WormsMap, worm: Worm): StandardCommandFeedback {
        val targetCell = gameMap[target]
        targetCell.type = CellType.AIR

        return StandardCommandFeedback(this.toString(), score = config.scores.dig, playerId = worm.player.id)
    }

    override fun toString(): String = "dig $target"

}
