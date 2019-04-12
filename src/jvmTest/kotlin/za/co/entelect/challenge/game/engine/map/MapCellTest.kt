package za.co.entelect.challenge.game.engine.map

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MapCellTest {

    @Test
    fun test_sorting() {
        val mapCells = (1..2).reversed().map { MapCell(it, it, CellType.AIR) }
        val sortedMapCells = mapCells.sortedWith(MapCell.comparator)

        assertTrue(sortedMapCells[0].x < sortedMapCells[1].x, "MapCell sorting is not functioning correctly")
    }

    @Test
    fun test_initial_properties_are_null() {
        val mapCell = MapCell(CellType.AIR)

        val prefixMessage = "MapCell property"
        val errMessageNullProperty = "was supposed to be null."
        assertNull(mapCell.powerup, "$prefixMessage powerup $errMessageNullProperty")
        assertNull(mapCell.occupier, "$prefixMessage occupier $errMessageNullProperty")
        assertNull(mapCell.occupierId, "$prefixMessage occupierId $errMessageNullProperty")
        assertNotNull(mapCell.ipInfo, "$prefixMessage ipInfo was not supposed to be null")
        assertNotNull(mapCell.nearCells, "$prefixMessage nearCells was not supposed to be null")
    }

}
