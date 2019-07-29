package za.co.entelect.challenge.game.engine.command.implementation

import za.co.entelect.challenge.game.engine.command.CommandStrings
import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.feedback.*
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm

class SnowballCommand(val target: Point, val config: GameConfig) : WormsCommand {

    override val order: Int = 3

    /**
     * For a snowball command to be valid:
     * * The worm must be a Technologist
     * * The worm must have snowballs to cast
     * * Target must be in map bounds
     * * The target cell must be within range
     */
    override fun validate(gameMap: WormsMap, worm: Worm): CommandValidation {
        return when {
            (worm.snowballs == null) -> CommandValidation.invalidMove("This worm is not technically adept in the arts of snowball fights")
            (worm.snowballs?.count == 0) -> CommandValidation.invalidMove("No snowballs bombs in inventory")
            (target !in gameMap) -> CommandValidation.invalidMove("$target out of map bounds")
            (target.shootingDistance(worm.position) > worm.snowballs?.range!!) -> CommandValidation.invalidMove("Cell $target is too far away")
            else -> CommandValidation.validMove()
        }
    }

    override fun execute(gameMap: WormsMap, worm: Worm): SnowballCommandFeedback {
        val wormSnowballs = worm.snowballs!!
        wormSnowballs.count = wormSnowballs.count - 1

        if (gameMap[target].type == CellType.DEEP_SPACE) {
            return SnowballCommandFeedback(toString(), worm, config.scores.missedAttack, SnowballResult.DEEP_SPACE, target, emptyList())
        }

        val freezeRadius = wormSnowballs.freezeRadius
        val freezeDuration = wormSnowballs.freezeDuration

        var wormsFrozen = 0
        val enemyWormHit = if (gameMap[target].isOccupied()) SnowballResult.BULLSEYE else SnowballResult.TERRAIN

        val iOffset = target.x - freezeRadius
        val jOffset = target.y - freezeRadius
        val affectedCells = mutableListOf<MapCell>()

        Point.getAllPointsOfASquare(0, freezeRadius * 2)
                .map { it + Point(iOffset, jOffset) }
                .forEach loop@{
                    if (it !in gameMap) {
                        return@loop // equivalent to continue
                    }

                    val cell = gameMap[it]
                    val distance = cell.position.manhattanDistance(target)
                    if (distance > freezeRadius * 2) {
                        return@loop // equivalent to continue
                    }

                    affectedCells.add(cell)
                    if (cell.isOccupied()) {
                        val occupier = cell.occupier!!
                        val isAlly = (occupier.player == worm.player)

                        occupier.setAsFrozen(freezeDuration)
                        when {
                            isAlly -> wormsFrozen--
                            else -> wormsFrozen++
                        }
                    }
                }

        val totalScore = (wormsFrozen * config.scores.freeze)

        return SnowballCommandFeedback(toString(), worm, totalScore, enemyWormHit, target, affectedCells)
    }

    override fun toString(): String {
        return "${CommandStrings.SNOWBALL.string} $target"
    }

}
