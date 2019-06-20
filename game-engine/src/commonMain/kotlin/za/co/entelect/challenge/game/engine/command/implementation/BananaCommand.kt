package za.co.entelect.challenge.game.engine.command.implementation

import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.feedback.*
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.CellType
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
 */
class BananaCommand(val target: Point, val config: GameConfig) : WormsCommand {

    override val order: Int = 3

    override fun validate(gameMap: WormsMap, worm: Worm): CommandValidation {
        return when {
            (worm.bananas == null) -> CommandValidation.invalidMove("This worm is not trained to use Banana bombs")
            (worm.bananas?.count == 0) -> CommandValidation.invalidMove("No bananas bombs in inventory")
            (target !in gameMap) -> CommandValidation.invalidMove("$target out of map bounds")
            (target.shootingDistance(worm.position) > worm.bananas?.range!!) -> CommandValidation.invalidMove("Cell $target is too far away")
            else -> CommandValidation.validMove()
        }
    }

    private fun getAllPointsOfSquare(start: Int, end: Int) =
            (start..end).flatMap { x -> (start..end).map { y -> Point(x, y) } }

    override fun execute(gameMap: WormsMap, worm: Worm): BananaCommandFeedback {
        val wormBananas = worm.bananas!!
        wormBananas.count = wormBananas.count - 1

        if (gameMap[target].type == CellType.DEEP_SPACE) {
            return BananaCommandFeedback(
                    this.toString(),
                    score = config.scores.missedAttack,
                    playerId = worm.player.id,
                    result = BananaResult.DEEP_SPACE,
                    target = target)
        }

        val damageRadius = wormBananas.damageRadius
        val damage = wormBananas.damage

        var totalDamageDone = 0
        var totalDirtDestroyed = 0
        val enemyWormHit = if (gameMap[target].isOccupied()) BananaResult.BULLSEYE else BananaResult.TERRAIN

        val iOffset = target.x - damageRadius
        val jOffset = target.y - damageRadius

        getAllPointsOfSquare(0, damageRadius * 2)
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

                    if (cell.type == CellType.DIRT) {
                        cell.type = CellType.AIR
                        totalDirtDestroyed += 1
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

        return BananaCommandFeedback(
                this.toString(),
                score = totalScore,
                playerId = worm.player.id,
                result = enemyWormHit,
                target = target)
    }

    override fun toString(): String {
        return "banana $target"
    }

}
