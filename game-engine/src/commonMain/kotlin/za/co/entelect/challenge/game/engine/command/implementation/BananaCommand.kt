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
import kotlin.math.roundToInt

/**
 * Destroys dirt
 * Gets dig score per block destroyed
 * X range
 * X damage
 * X damage radius
 * Splash damage with smooth drop off intensity
 * Can you throw over dirt (this is now called banana)
 * Not Directions, use coordinates
 * Friendly fire
 * Used to throws the banana bomb
 *
 * Command to throw a Banana Bomb
 */
class BananaCommand(val target: Point, val config: GameConfig) : WormsCommand {

    override val order: Int = 3

    /**
     * For a banana command to be valid:
     * * The worm must be an Agent
     * * The worm must have bananas to throw
     * * Target must be in map bounds
     * * The target cell must be within range
     */
    override fun validate(gameMap: WormsMap, worm: Worm): CommandValidation {
        return when {
            (worm.bananas == null) -> CommandValidation.invalidMove("This worm is not trained to use Banana bombs")
            (worm.bananas?.count == 0) -> CommandValidation.invalidMove("No bananas bombs in inventory")
            (target !in gameMap) -> CommandValidation.invalidMove("$target out of map bounds")
            (target.shootingDistance(worm.position) > worm.bananas?.range!!) -> CommandValidation.invalidMove("Cell $target is too far away")
            else -> CommandValidation.validMove()
        }
    }

    override fun execute(gameMap: WormsMap, worm: Worm): BananaCommandFeedback {
        val wormBananas = worm.bananas!!
        wormBananas.count = wormBananas.count - 1

        if (gameMap[target].type == CellType.DEEP_SPACE) {
            return BananaCommandFeedback(toString(), worm, config.scores.missedAttack, BananaResult.DEEP_SPACE, target, emptyList())
        }

        val damageRadius = wormBananas.damageRadius
        val damage = wormBananas.damage

        var totalDamageDone = 0
        var totalDirtDestroyed = 0
        val enemyWormHit = if (gameMap[target].isOccupied()) BananaResult.BULLSEYE else BananaResult.TERRAIN

        val iOffset = target.x - damageRadius
        val jOffset = target.y - damageRadius
        val affectedCells = mutableListOf<MapCell>()

        Point.getAllPointsOfASquare(0, damageRadius * 2)
                .map { it + Point(iOffset, jOffset) }
                .forEach loop@{
                    if (it !in gameMap) {
                        return@loop // equivalent to continue
                    }

                    val cell = gameMap[it]
                    val distance = cell.position.euclideanDistance(target)
                    if (distance > damageRadius) {
                        return@loop // equivalent to continue
                    }

                    if (cell.type == CellType.DIRT || cell.destroyedInRound == gameMap.currentRound) {
                        cell.type = CellType.AIR
                        cell.destroyedInRound = gameMap.currentRound
                        totalDirtDestroyed += 1
                        affectedCells.add(cell)
                    }

                    if (cell.type == CellType.LAVA) {
                        cell.type = CellType.DEEP_SPACE
                        // Worms on top of lava become instant killed by banana bombs
                        if (cell.isOccupied()) {
                            val occupier = cell.occupier!!
                            occupier.takeDamage(occupier.health, gameMap.currentRound, worm.player)

                            val isAlly = (occupier.player == worm.player)
                            when {
                                isAlly -> totalDamageDone -= occupier.health
                                else -> totalDamageDone += occupier.health
                            }
                        }
                    }

                    cell.powerup = null

                    if (cell.isOccupied()) {
                        val occupier = cell.occupier!!
                        val isAlly = (occupier.player == worm.player)

                        val specialDamageRadius = damageRadius + 1
                        val damageTier = damage * ((specialDamageRadius - distance) / specialDamageRadius)

                        val damageToTarget = occupier.takeDamage(damageTier.roundToInt(), gameMap.currentRound, worm.player)
                        when {
                            isAlly -> totalDamageDone -= damageToTarget
                            else -> totalDamageDone += damageToTarget
                        }
                    }
                }

        val totalScore = (totalDirtDestroyed * config.scores.dig + totalDamageDone * config.scores.attack)

        return BananaCommandFeedback(toString(), worm, totalScore, enemyWormHit, target, affectedCells)
    }

    override fun toString(): String {
        return "${CommandStrings.BANANA.string} $target"
    }

}
