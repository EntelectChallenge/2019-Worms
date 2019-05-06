package za.co.entelect.challenge.game.engine.map

import za.co.entelect.challenge.game.engine.command.feedback.CommandFeedback
import za.co.entelect.challenge.game.engine.player.Worm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.processor.GameError

interface GameMap {
    val players: List<WormsPlayer>
    val livingPlayers: List<WormsPlayer>
    /**
     * The winning player or null if the game ended in a tie
     */
    val winningPlayer: WormsPlayer?

    val cells: List<MapCell>
    var currentRound: Int
    val currentRoundErrors: List<GameError>
    val currentRoundFeedback: List<CommandFeedback>

    operator fun contains(target: Point): Boolean

    operator fun get(target: Point): MapCell

    operator fun get(x: Int, y: Int): MapCell

    fun addError(gameError: GameError)
    fun addFeedback(feedback: CommandFeedback)

    fun removeDeadWorms()
    fun applyHealthPacks()

}

class WormsMap(override val players: List<WormsPlayer>,
               val size: Int,
               cells: List<MapCell>) : GameMap {

    val allFeedback = mutableMapOf<Int, MutableList<CommandFeedback>>()
    override val currentRoundFeedback: List<CommandFeedback>
        get() = allFeedback[currentRound].orEmpty()

    override var currentRound: Int = 0
    override val cells: List<MapCell>

    private val errorList = mutableListOf<GameError>()
    override val currentRoundErrors
        get() = errorList.filter { it.round == currentRound }

    private val xRange = 0 until size
    private val yRange = 0 until size

    override val livingPlayers: List<WormsPlayer>
        get() = players.filter { !it.dead && !it.disqualified }

    /**
     * The winning player or null if the game ended in a tie
     */
    override val winningPlayer: WormsPlayer?
        get() {
            return when {
                livingPlayers.size > 1 -> maxByScore(livingPlayers)
                livingPlayers.isEmpty() -> maxByScore(players)
                else -> livingPlayers[0]
            }
        }

    init {
        val requiredSize = size * size
        if (cells.size != requiredSize) {
            throw IllegalArgumentException("Need $requiredSize cells to fill the map, received ${cells.size}")
        } else {
            this.cells = cells.sortedWith(MapCell.comparator)
        }

        players.forEach { player -> player.worms.forEach { placeWorm(it) } }
    }

    override operator fun contains(target: Point): Boolean {
        return target.x in xRange && target.y in yRange
    }

    override operator fun get(target: Point): MapCell {
        return this[target.x, target.y]
    }

    override operator fun get(x: Int, y: Int): MapCell {
        if (x !in xRange) {
            throw IndexOutOfBoundsException("x=$x out of range $xRange")
        }

        if (y !in yRange) {
            throw IndexOutOfBoundsException("y=$y out of range $yRange")
        }

        return cells[y * size + x]
    }

    private fun placeWorm(worm: Worm) {
        this[worm.position].occupier = worm
    }

    override fun addError(gameError: GameError) {
        errorList.add(gameError)
    }

    override fun addFeedback(feedback: CommandFeedback) {
        allFeedback.getOrPut(currentRound) { mutableListOf() }.add(feedback)
    }

    override fun removeDeadWorms() {
        players.flatMap { it.worms }
                .filter { it.dead || it.player.disqualified }
                .forEach { removeWorm(it) }
    }

    private fun maxByScore(players: List<WormsPlayer>): WormsPlayer? {
        val highestScoringPlayers = players.groupBy { it.totalScore }.maxBy { it.key }?.value

        if (highestScoringPlayers == null || highestScoringPlayers.size != 1) {
            return null
        }

        return highestScoringPlayers[0]
    }

    private fun removeWorm(worm: Worm) {
        val mapCell = get(worm.position)

        if (mapCell.occupier == worm) {
            mapCell.occupier = null
        }
    }

    fun isOutOfBounds(target: Point): Boolean {
        return (target.x !in xRange) || (target.y !in yRange)
    }

    override fun applyHealthPacks() {
        /**
         * Right now we only have single use powerups. If that changes,
         * we can move the clearing logic into the powerup `applyTo` method
         */
        players.flatMap { it.worms }
                .forEach { worm ->
                    val cell = this[worm.position]
                    if (cell.occupier == worm) {
                        cell.powerup?.applyTo(worm)
                        cell.powerup = null
                    }
                }
    }
}

