package za.co.entelect.challenge.game.engine.map

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.simplexNoise.SimplexNoise
import java.lang.Math.pow
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.test.*

class WormsMapGeneratorTest {

    private val config: GameConfig = TEST_CONFIG

    /**
     * Get 2 players with 3 worms each
     */
    private fun getPlayers2Worms3() = (1..2).map {
        val playerSquad = (1..3).map { wormIndex ->
            CommandoWorm.build(wormIndex, config)
        }

        WormsPlayer.build(it, playerSquad, config)
    }

    private fun getMapCenter(config: GameConfig): Pair<Double, Double> =
            Pair((((config.mapSize + 1) / 2) - 0.5), ((config.mapSize + 1) / 2) - 0.5)

    @Test
    fun test_generated_map_cells_have_worms() {
        val wormsMapGenerator = WormsMapGenerator(config, 0)
        val wormsMap = wormsMapGenerator.getMap(getPlayers2Worms3())

        assertEquals(wormsMap.cells.size, config.mapSize * config.mapSize,
                "Map generated does not contain expected amount of cells,\n" +
                        "has ${wormsMap.cells.size} cells \n expected ${config.mapSize * config.mapSize} cells")

        val wormPositions = listOf(
                Point(23, 29),
                Point(0, 16),
                Point(23, 2),
                Point(31, 16),
                Point(8, 29),
                Point(8, 2)
        )

        val mapSpawnError = "Check if MapConfig, map alignment, or spawn rules changed."
        wormPositions.map { point -> wormsMap[point].occupier }
                .forEach { occupier ->
                    assertNotNull(occupier, "No Worm found at old constant coordinates \n" +
                            mapSpawnError)
                }

        wormsMap.cells
                .filter { cell -> !wormPositions.any { wormPosition -> cell.position == wormPosition } }
                .forEach { cell ->
                    assertNull(cell.occupier, "Expected No Worm at (${cell.position})" +
                            mapSpawnError)
                }
    }

    @Test
    fun test_worms_have_open_spawn_area() {
        val players = listOf(WormsPlayer.build(1, listOf(CommandoWorm.build(0, config)), config))

        val wormsMapGenerator = WormsMapGenerator(config, 0)
        val wormsMap = wormsMapGenerator.getMap(players)

        wormsMap.players
                .flatMap { it.worms }
                .forEach { w ->
                    val centerCell = wormsMap[w.position]
                    centerCell.nearCells
                            .getAllCells()
                            .union(listOf(centerCell))
                            .forEach {
                                assertTrue(listOf(CellType.AIR, CellType.DEEP_SPACE).contains(it.type),
                                        "Expected CellType.AIR || DEEP_SPACE at ${it.position}")
                            }
                }
    }

    @Test
    fun test_worms_squad_spawns_scattered() {
        val wormsMapGenerator = WormsMapGenerator(config, 0)
        val wormsMap = wormsMapGenerator.getMap(getPlayers2Worms3())

        val playerWormInterDistances = wormsMap.players
                .map { p ->
                    p.worms.map { w ->
                        p.worms.filter { otherWorm -> otherWorm != w }
                                .map { otherWorm -> otherWorm.position.euclideanDistance(w.position) }
                                .average()
                    }
                }

        playerWormInterDistances.forEachIndexed { index, sqaud ->
            val averageMapDistance = listOf(config.mapSize, config.mapSize).average()
            val squadSD = standardDeviation(sqaud)
            assertTrue(squadSD < (averageMapDistance * 0.1), "Worms in squad $index are not equidistant from each other")
        }
    }

    @Test
    fun test_powerups_spawned() {
        val wormsMapGenerator = WormsMapGenerator(config, 0)
        val wormsMap = wormsMapGenerator.getMap(getPlayers2Worms3())
        val (xMid, yMid) = getMapCenter(config)

        // percentage distance of map that powerups are not allowed to spawn in
        // distance measured radially, where outside should be without powerups
        val percentage = 0.25

        wormsMap.cells
                .filter { it.powerup != null }
                .map { it.position }
                .forEach { (x, y) ->
                    assertTrue(
                            abs(x - xMid) < (config.mapSize) * percentage
                                    && abs(y - yMid) < (config.mapSize) * percentage,
                            "Powerup spawned too far from center, at x:$x y:$y. Check map generator or config")
                }
    }

    @Test
    fun test_renderer() {
        val wormsMapGenerator = WormsMapGenerator(config, 0)
        val stringMap = wormsMapGenerator.printMapInPierreCharacters(listOf(listOf(
                MapCell(CellType.AIR),
                MapCell(CellType.DIRT),
                MapCell(CellType.DEEP_SPACE))))
        assertTrue(stringMap.length > 6, "Map renderer has broken")
    }

    @Test
    fun test_procedural_random() {
        val noise = SimplexNoise(42)

        // The expected value for SimplexNoise given,
        // seed 42, x 1.0, y 1.0
        // = 0.06214340506192554
        assertEquals(noise.n2d(1.0, 1.0), 0.06214340506192554)

        assertEquals(noise.n2d(64.3, 64.3), 0.4928681547832553)
        assertEquals(noise.n2d(254.9, 212.21), 0.2528798486304122)
        assertEquals(noise.n2d(-51.4, 128.9), 0.7977717117332975)
    }

    private fun standardDeviation(elements: List<Double>): Double {
        val mean = elements.sorted().get(elements.size / 2)
        val deviationSquared = elements.fold(0.0) { sum, i -> sum + pow(i - mean, 2.0) }
        return sqrt(deviationSquared / (elements.size - 1))
    }

}
