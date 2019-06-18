package za.co.entelect.challenge.game.engine.map

import mu.KotlinLogging
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.powerups.HealthPack
import za.co.entelect.challenge.game.engine.simplexNoise.SimplexNoise
import kotlin.math.*

class WormsMapGenerator(private val config: GameConfig, private val seed: Int) {

    private val noise = SimplexNoise(seed)
    private val mapZoom = 0.3
    private val amountOfDirt = 0.50
    private var mapCenter: Pair<Double, Double>
    private var mapRadiusFit: Double
    private val wormSpawnDistanceFromEdge = 1

    init {
        mapCenter = getMapCenter(config)
        mapRadiusFit = getMapRadiusFit(mapCenter)
    }

    private fun getMapCenter(config: GameConfig): Pair<Double, Double> {
        val center = (config.mapSize - 1) / 2.0
        return Pair(center, center)
    }

    private fun getMapRadiusFit(mapCenter: Pair<Double, Double>): Double = min(mapCenter.first, mapCenter.second)

    fun getMap(wormsPlayers: List<WormsPlayer>): WormsMap {
        logger.info { "Generating Map: Size=${config.mapSize}, Seed=$seed" }

        logger.info { "Generating blank map" }
        val blankMap = (0 until config.mapSize).map { i ->
            (0 until config.mapSize).map { j -> MapCell(i, j, CellType.AIR) }
        }
        val flatBlankMap = blankMap.flatten()

        logger.info { "Populating cell types" }
        flatBlankMap.forEach { populateCell(it) }

        logger.info { "Placing worms" }
        setWormsIntoSpawnLocations(blankMap, wormsPlayers)

        logger.info { "Creating worm rooms" }
        createWormWalledSpawnLocations(wormsPlayers, blankMap)

        logger.info { "Creating map boundary" }
        setBattleRoyaleMapEdges(flatBlankMap)

        logger.info { "Placing powerups" }
        placePowerups(blankMap, 2.2, config.totalHealthPacks)

        logger.info { "Map generation finished" }
        return WormsMap(wormsPlayers,
                config.mapSize,
                flatBlankMap)
    }

    private fun placePowerups(blankMap: List<List<MapCell>>, radius: Double, count: Int) {
        getCirclePositions(blankMap, radius, count, noise.n1d(count))
                .forEach {
                    it.powerup = HealthPack.build(config)
                    it.type = CellType.AIR
                }
    }

    private fun setBattleRoyaleMapEdges(flatBlankMap: List<MapCell>) {
        flatBlankMap.filter { it.position.euclideanDistance(mapCenter) > mapRadiusFit + 1 }
                .forEach { it.type = CellType.DEEP_SPACE }
    }

    /**
     * Create circle with equidistant seats placed on the circle.
     * Returns the MapCell for each position
     */
    private fun getCirclePositions(blankMap: List<List<MapCell>>,
                                   radius: Double,
                                   count: Int,
                                   tilt: Double = 0.0): List<MapCell> {
        return (0 until count)
                .map {
                    val t = 2.0 * PI * (it.toDouble() / count.toDouble()) + tilt
                    val x = radius * cos(t) + mapCenter.first
                    val y = radius * sin(t) + mapCenter.second
                    getCellAt(roundCoordinatesToIntByAngle(t, x, y), blankMap)!!
                }
    }

    private fun roundCoordinatesToIntByAngle(angle: Double, x: Double, y: Double): Point {
        val angleCapped = angle % (2.0 * PI)
        return when {
            angleCapped <= (PI * 0.5) -> Point(ceil(x).toInt(), floor(y).toInt())
            angleCapped <= (PI * 1.0) -> Point(floor(x).toInt(), floor(y).toInt())
            angleCapped <= (PI * 1.5) -> Point(floor(x).toInt(), ceil(y).toInt())
            else -> Point(ceil(x).toInt(), ceil(y).toInt())
        }
    }

    private fun createWormWalledSpawnLocations(wormsPlayers: List<WormsPlayer>,
                                               blankMap: List<List<MapCell>>) {
        // Draw the spawnroom here. Only odd numbered widths can be centered
        val dirtChar = '#'.toString()
        val spawnRoom = """
            |#####
            |#...#
            |#...#
            |#...#
            |#####
            """.trimMargin()
                .lines()
                .mapIndexed { y, line ->
                    val offset = line.length / 2
                    line.trim().split("")
                            .filter { it.isNotEmpty() }
                            .mapIndexed { x, char ->
                                val cellType = if (char == dirtChar) CellType.DIRT else CellType.AIR
                                Pair(Point(x - offset, y - offset), cellType)
                            }
                }.flatten()

        joinLists(wormsPlayers.flatMap { it.worms }, spawnRoom)
                .map { Triple(it.first, it.second.first, it.second.second) }
                .forEach { (w, pointDelta, cellType) ->
                    getCellAt(w.position + pointDelta, blankMap)?.type = cellType
                }
    }

    private fun getCellAt(point: Point, blankMap: List<List<MapCell>>): MapCell? =
            blankMap.getOrNull(point.x)?.getOrNull(point.y)

    private fun <T, S> joinLists(aList: List<T>, bList: List<S>): Sequence<Pair<T, S>> = sequence {
        aList.forEach { aItem ->
            bList.forEach { bItem ->
                yield(Pair(aItem, bItem))
            }
        }
    }

    private fun setWormsIntoSpawnLocations(blankMap: List<List<MapCell>>,
                                           wormsPlayers: List<WormsPlayer>) {
        val allWorms = wormsPlayers
                .flatMap { it.worms }
        val unplacedWorms = allWorms
                .groupBy { it.player.id }
                .mapValues { (_, value) -> value.toMutableList() }

        // Put worms in seats, a different player for the next seat
        val radius = mapRadiusFit - wormSpawnDistanceFromEdge
        val count = allWorms.size

        logger.info { "Placing $count worms in radius $radius" }

        getCirclePositions(blankMap, radius, count)
                .forEachIndexed { index, seat ->
                    val playerIndex = ((index + 1) % wormsPlayers.size)

                    logger.debug { "Placing worm: Index=$index, Player=$playerIndex" }
                    val player = wormsPlayers[playerIndex]
                    val wormToPlace = unplacedWorms.getValue(player.id).removeAt(0)

                    wormToPlace.initPositions(seat.position)
                    seat.occupier = wormToPlace

                    logger.debug { "Placed worm: $wormToPlace in seat=$seat" }
                }
    }

    private fun populateCell(cell: MapCell) {
        logger.debug { "Populating cell $cell" }

        val mapSize = config.mapSize - 1
        val x = if (cell.x > mapSize / 2) mapSize - cell.x else cell.x

        val noiseValue = noise.n2d(x * mapZoom, cell.y * mapZoom)

        cell.ipInfo.srcValue = noiseValue
        cell.type = if (cell.ipInfo.srcValue!! > amountOfDirt) CellType.AIR else CellType.DIRT

        logger.debug { "Populated cell $cell based on noise $noiseValue" }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
