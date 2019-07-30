package za.co.entelect.challenge.game.engine.map

import za.co.entelect.challenge.game.engine.command.feedback.*
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.player.Worm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.processor.GameError
import za.co.entelect.challenge.game.engine.renderer.printables.VisualizerEvent
import kotlin.math.PI
import kotlin.math.sin

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

    operator fun contains(target: Point): Boolean

    operator fun get(target: Point): MapCell

    operator fun get(x: Int, y: Int): MapCell

    fun addError(gameError: GameError)
    fun addFeedback(feedback: CommandFeedback)
    fun getFeedback(round: Int): List<CommandFeedback>

    fun removeDeadWorms()
    fun applyHealthPacks()

    fun detectRefereeIssues()
    fun getRefereeIssues(): List<String>
    fun setScoresForKilledWorms(config: GameConfig)
    fun getVisualizerEvents(): List<VisualizerEvent>
    fun progressBattleRoyale(config: GameConfig)
    fun tickFrozenTimers()
}

class WormsMap(override val players: List<WormsPlayer>,
               val size: Int,
               cells: List<MapCell>) : GameMap {
    private val allFeedback = mutableMapOf<Int, MutableList<CommandFeedback>>()

    override var currentRound: Int = 0
    override val cells: List<MapCell>

    private val errorList = mutableListOf<GameError>()
    override val currentRoundErrors
        get() = errorList.filter { it.round == currentRound }

    private val xRange = 0 until size
    private val yRange = 0 until size

    private val refereeIssues = mutableListOf<String>()

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

    override fun getFeedback(round: Int): List<CommandFeedback> {
        return allFeedback[round] ?: emptyList()
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

    override fun detectRefereeIssues() {
        val doNothingsCountLimit = 3
        livingPlayers.forEach {
            if (it.consecutiveDoNothingsCount == doNothingsCountLimit) {
                refereeIssues.add("DoNothingsCount for @Player(${it.id}) " +
                        "reached a count of $doNothingsCountLimit @Round($currentRound)")
            }
        }
    }

    override fun getRefereeIssues(): List<String> {
        return refereeIssues
    }

    override fun setScoresForKilledWorms(config: GameConfig) {
        players.flatMap { it.worms }
                .filter { it.dead && it.lastAttackedBy.any() }
                .flatMap { worm -> worm.lastAttackedBy.distinct().map { attacker -> Pair(worm, attacker) } }
                .forEach { (worm, attacker) ->
                    when (attacker) {
                        worm.player -> attacker.commandScore -= config.scores.killShot
                        else -> attacker.commandScore += config.scores.killShot
                    }
                }

        players.flatMap { it.worms }.forEach { it.lastAttackedBy.clear() }
    }

    override fun getVisualizerEvents(): List<VisualizerEvent> {
        return allFeedback[currentRound]?.mapNotNull { it.visualizerEvent } ?: emptyList()
    }

    override fun progressBattleRoyale(config: GameConfig) {
        val center = (config.mapSize - 1) / 2.0
        val mapCenter = Pair(center, center)

        val brStartRound = config.maxRounds * config.battleRoyaleStart
        if (currentRound < brStartRound) {
            return
        }
        val brEndRound = config.maxRounds * config.battleRoyaleEnd
        val fullPercentageRange = (currentRound - brStartRound) / (brEndRound - brStartRound)
        val currentProgress = fullPercentageRange.coerceIn(0.0, 1.0)

        val safeAreaRadius = (config.mapSize / 2) * (1 - currentProgress)

        cells.filter { it.type == CellType.AIR && it.position.euclideanDistance(mapCenter) > safeAreaRadius + 1 }
                .forEach { it.type = CellType.LAVA }

        livingPlayers.flatMap { it.livingWorms }
                .filter { cells.any { cell -> cell.type == CellType.LAVA && cell.position == it.position } }
                .forEach { worm -> worm.takeDamage(config.lavaDamage, currentRound) }
    }

    override fun tickFrozenTimers() {
        this.livingPlayers.flatMap { it.livingWorms }.forEach { it.tickFrozenTimer() }
    }

}

