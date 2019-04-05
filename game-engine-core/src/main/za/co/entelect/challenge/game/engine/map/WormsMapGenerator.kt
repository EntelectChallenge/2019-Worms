package za.co.entelect.challenge.game.engine.map

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.powerups.HealthPack
import za.co.entelect.challenge.game.engine.powerups.Powerup
import za.co.entelect.challenge.game.engine.simplexNoise.SimplexNoise
import kotlin.math.*

class WormsMapGenerator(private val config: GameConfig, private val seed: Long) {

    private val noise = SimplexNoise(seed.toInt())
    private val mapZoom = 0.3
    private val amountOfSoil = 0.50
    private var mapCenter: Pair<Double, Double> = Pair(0.0, 0.0)
    private var mapRadiusFit: Double = 0.0
    private val wormSpawnDistanceFromEdge = 1

    init {
        mapCenter = getMapCenter(config)
        mapRadiusFit = getMapRadiusFit(mapCenter)
    }

    private fun getMapCenter(config: GameConfig): Pair<Double, Double> {
        val center = ((config.mapSize + 1) / 2) - 0.5
        return Pair(center, center)
    }

    private fun getMapRadiusFit(mapCenter: Pair<Double, Double>): Double = min(mapCenter.first, mapCenter.second) + 1

    fun getMap(wormsPlayers: List<WormsPlayer>): WormsMap {

        val blankMap = (0 until config.mapSize).map { i ->
            (0 until config.mapSize).map { j -> MapCell(i, j) }
        }
        val flatBlankMap = blankMap.flatten()

        flatBlankMap.forEach { populateCell(it, blankMap) }
        val drumcircleSeatPositions = getSpawnLocations(wormsPlayers, flatBlankMap)
        setWormsIntoSpawnLocations(drumcircleSeatPositions, wormsPlayers)
        createWormWalledSpawnLocations(wormsPlayers, blankMap)
        setBattleRoyaleMapEdges(flatBlankMap)
        placePowerups(wormsPlayers, blankMap)

        return WormsMap(wormsPlayers,
                config.mapSize,
                flatBlankMap)
    }

    private fun placePowerups(wormsPlayers: List<WormsPlayer>, blankMap: List<List<MapCell>>) {
        val wormsCount = wormsPlayers.flatMap { it.worms }.size
        val maxSpotsPerCircle = 10
        val spiralStretchFactor = 15
        val powerupSpawnThreshold = 0.56

        (0..wormsCount).map { Pair(it, log((it * 0.1) + 1.1, 10.0) * spiralStretchFactor) }
                .map { (i, radius) ->
                    // Parameterized circle. Create positions on a spiral (centered on map center)
                    // with added randomizer value
                    val t = 2 * PI * i / (maxSpotsPerCircle * noise.n1d(i))
                    val x = (radius * cos(t) + mapCenter.first).roundToInt()
                    val y = (radius * sin(t) + mapCenter.second).roundToInt()
                    val procRandom = noise.n2d(i, maxSpotsPerCircle)
                    Triple(x, y, procRandom)
                }
                .filter { (_, _, procRandom) -> procRandom > powerupSpawnThreshold }
                .map { (x, y, _) -> blankMap[x][y] }
                .forEach { cell ->
                    cell.powerup = HealthPack.build(config)
                    cell.type = CellType.AIR
                }
    }

    private fun setBattleRoyaleMapEdges(flatBlankMap: List<MapCell>) {
        flatBlankMap.filter { euclideanDistance(mapCenter, it.position) >= mapRadiusFit }
                .forEach { it.type = CellType.DEEP_SPACE }
    }

    private fun euclideanDistance(a: Pair<Double, Double>, b: Point): Double {
        return euclideanDistance(a, Pair(b.x.toDouble(), b.y.toDouble()))
    }

    private fun euclideanDistance(a: Pair<Double, Double>, b: Pair<Double, Double>): Double {
        return sqrt((a.first - b.first).pow(2) + (a.second - b.second).pow(2))
    }

    private fun createWormWalledSpawnLocations(wormsPlayers: List<WormsPlayer>,
                                               blankMap: List<List<MapCell>>) {
        // 5x5 square cell room around the worm. The border is made of DIRT,
        // everything inside is made of AIR
        val spawnRoom = listOf(
                Pair(Point(-1, -1), CellType.AIR),
                Pair(Point(-1, 0), CellType.AIR),
                Pair(Point(-1, 1), CellType.AIR),
                Pair(Point(0, -1), CellType.AIR),
                Pair(Point(0, 0), CellType.AIR),
                Pair(Point(0, 1), CellType.AIR),
                Pair(Point(1, -1), CellType.AIR),
                Pair(Point(1, 0), CellType.AIR),
                Pair(Point(1, 1), CellType.AIR),

                Pair(Point(-2, -2), CellType.DIRT),
                Pair(Point(-2, -1), CellType.DIRT),
                Pair(Point(-2, 0), CellType.DIRT),
                Pair(Point(-2, 1), CellType.DIRT),
                Pair(Point(-2, 2), CellType.DIRT),

                Pair(Point(-1, -2), CellType.DIRT),
                Pair(Point(0, -2), CellType.DIRT),
                Pair(Point(1, -2), CellType.DIRT),

                Pair(Point(2, -2), CellType.DIRT),
                Pair(Point(2, -1), CellType.DIRT),
                Pair(Point(2, 0), CellType.DIRT),
                Pair(Point(2, 1), CellType.DIRT),
                Pair(Point(2, 2), CellType.DIRT),

                Pair(Point(-1, 2), CellType.DIRT),
                Pair(Point(0, 2), CellType.DIRT),
                Pair(Point(1, 2), CellType.DIRT)
        )

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

    private fun setWormsIntoSpawnLocations(drumcircleSeatPositions: List<MapCell>,
                                           wormsPlayers: List<WormsPlayer>) {
        val unplacedWorms = wormsPlayers.flatMap { it.worms }
                .groupBy { it.player.id }
                .mapValues { (_, value) -> value.toMutableList() }

        // Put worms in seats, a different player for the next seat
        drumcircleSeatPositions.forEachIndexed { index, seat ->
            val playerIndex = ((index + 1) % wormsPlayers.size)
            val player = wormsPlayers[playerIndex]

            val wormToPlace = unplacedWorms.getValue(player.id).removeAt(0)

            wormToPlace.initPositions(seat.position)
            seat.occupier = wormToPlace
        }
    }

    private fun getSpawnLocations(wormsPlayers: List<WormsPlayer>,
                                  flatBlankMap: List<MapCell>): List<MapCell> {
        // Create circle with equidistant seats
        val drumcircleRadius = mapRadiusFit - wormSpawnDistanceFromEdge
        val seatCount = wormsPlayers.flatMap { it.worms }.size
        val drumcircleSeatPositions = (0 until seatCount)
                .map { i ->
                    val t = 2 * PI * (i.toDouble() / seatCount.toDouble())
                    val x = (drumcircleRadius * cos(t) + mapCenter.first).roundToInt()
                    val y = (drumcircleRadius * sin(t) + mapCenter.second).roundToInt()
                    flatBlankMap.first { cell -> cell.x == x && cell.y == y }
                }
        return drumcircleSeatPositions
    }

    private fun populateCell(cell: MapCell, blankMap: List<List<MapCell>>) {
        cell.nearCells.setAllNearCells(blankMap, cell)

        cell.ipInfo.srcValue = noise.n2d(cell.x * mapZoom, cell.y * mapZoom)
        cell.type = if (cell.ipInfo.srcValue!! > amountOfSoil) CellType.AIR else CellType.DIRT
    }

}
