package za.co.entelect.challenge.game.engine.command.implementation

import za.co.entelect.challenge.game.engine.command.CommandStrings
import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.feedback.StandardCommandFeedback
import za.co.entelect.challenge.game.engine.command.feedback.CommandValidation
import za.co.entelect.challenge.game.engine.command.feedback.DigCommandFeedback
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
        if (target !in gameMap) return CommandValidation.invalidMove("$target out of map bounds")

        val targetCell = gameMap[target]
        return when {
            !targetCell.type.diggable -> CommandValidation.invalidMove("Cell type ${targetCell.type} not diggable")
            (target.movementDistance(worm.position) > worm.diggingRange) -> CommandValidation.invalidMove("Cell $target too far away")
            else -> CommandValidation.validMove()
        }
    }

    override fun execute(gameMap: WormsMap, worm: Worm): DigCommandFeedback {
        val targetCell = gameMap[target]
        targetCell.type = CellType.AIR

        return DigCommandFeedback(toString(), worm, config.scores.dig, target)
    }

    override fun toString(): String = "${CommandStrings.DIG.string} $target"

}
