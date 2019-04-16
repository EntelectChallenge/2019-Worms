package za.co.entelect.challenge.game.engine.map

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.powerups.HealthPack
import za.co.entelect.challenge.game.engine.simplexNoise.SimplexNoise
import kotlin.math.*

class WormsMapGenerator(private val config: GameConfig, seed: Int) {

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
        val blankMap = (0 until config.mapSize).map { i ->
            (0 until config.mapSize).map { j -> MapCell(i, j) }
        }
        val flatBlankMap = blankMap.flatten()

        flatBlankMap.forEach { populateCell(it) }
        setWormsIntoSpawnLocations(blankMap, wormsPlayers)
        createWormWalledSpawnLocations(wormsPlayers, blankMap)
        setBattleRoyaleMapEdges(flatBlankMap)
        placePowerups(blankMap, 2.2, config.totalHealthPacks)

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
                    getCellAt(Point(x.roundToInt(), y.roundToInt()), blankMap)!!
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
        val unplacedWorms = wormsPlayers.flatMap { it.worms }
                .groupBy { it.player.id }
                .mapValues { (_, value) -> value.toMutableList() }

        // Put worms in seats, a different player for the next seat
        val radius = mapRadiusFit - wormSpawnDistanceFromEdge
        val count = wormsPlayers.flatMap { it.worms }.size
        getCirclePositions(blankMap, radius, count)
                .forEachIndexed { index, seat ->
                    val playerIndex = ((index + 1) % wormsPlayers.size)
                    val player = wormsPlayers[playerIndex]

                    val wormToPlace = unplacedWorms.getValue(player.id).removeAt(0)

                    wormToPlace.initPositions(seat.position)
                    seat.occupier = wormToPlace
                }
    }

    private fun populateCell(cell: MapCell) {
        val mapSize = config.mapSize - 1
        val x = if (cell.x > mapSize / 2) mapSize - cell.x else cell.x

        cell.ipInfo.srcValue = noise.n2d(x * mapZoom, cell.y * mapZoom)
        cell.type = if (cell.ipInfo.srcValue!! > amountOfDirt) CellType.AIR else CellType.DIRT
    }

}
