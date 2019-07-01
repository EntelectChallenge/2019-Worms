package za.co.entelect.challenge.game.engine.map

import za.co.entelect.challenge.game.delegate.factory.GameConfigFactory
import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.factory.TestMapFactory.getMapCenter
import za.co.entelect.challenge.game.engine.factory.TestMapFactory.standardDeviation
import za.co.entelect.challenge.game.engine.factory.TestWormsPlayerFactory.buildWormsPlayers
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.simplexNoise.SimplexNoise
import kotlin.math.abs
import kotlin.test.*

class WormsMapGeneratorTest {

    private val config: GameConfig = TEST_CONFIG

    @Test
    fun test_generated_map_cells_have_worms() {
        val wormsMapGenerator = WormsMapGenerator(config, 0)
        val wormsMap = wormsMapGenerator.getMap(buildWormsPlayers(config, 2, 3))

        assertEquals(wormsMap.cells.size, config.mapSize * config.mapSize,
                "Map generated does not contain expected amount of cells,\n" +
                        "has ${wormsMap.cells.size} cells \n expected ${config.mapSize * config.mapSize} cells")

        val wormPositions = listOf(
                Point(24, 28),
                Point(1, 16),
                Point(24, 4),
                Point(31, 16),
                Point(8, 28),
                Point(8, 4)
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

        val wormsMap = WormsMapGenerator(config, 0).getMap(players)
        wormsMap.players
                .flatMap { it.worms }
                .forEach { w ->
                    (-1..1).flatMap { i -> (-1..1).map { j -> Point(i, j) } }
                            .map { w.position + it }
                            .filter { it in wormsMap }
                            .map { wormsMap[it] }
                            .forEach {
                                assertTrue(listOf(CellType.AIR, CellType.DEEP_SPACE).contains(it.type),
                                        "Expected CellType AIR or DEEP_SPACE at ${it.position}")
                            }
                }
    }

    @Test
    fun test_worms_squad_spawns_scattered() {
        val wormsMapGenerator = WormsMapGenerator(config, 0)
        val wormsMap = wormsMapGenerator.getMap(buildWormsPlayers(config, 2, 3))

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
        val wormsMap = wormsMapGenerator.getMap(buildWormsPlayers(config, 2, 3))
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

    @Test
    fun test_small_map_symmetrical() {
        val editConfig = GameConfigFactory.getConfig("src/jvmTest/resources/test-config-small-map.json")

        val wormsMapGenerator = WormsMapGenerator(editConfig, 0)
        val wormsMap = wormsMapGenerator.getMap(buildWormsPlayers(editConfig, 2, 3))

        val visualMap = getAllPointsOfSquare(0, 12).map { wormsMap[it].type }
                .chunked(13)
                .joinToString(separator = "\n") { line -> line.joinToString(separator = "") { it.printable } }

        println()
    }

    private fun getAllPointsOfSquare(start: Int, end: Int) =
            (start..end).flatMap { x -> (start..end).map { y -> Point(x, y) } }

}
