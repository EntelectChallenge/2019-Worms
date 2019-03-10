package za.co.entelect.challenge.game.engine.map

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NearCellsTest {

    @Test
    fun test_properties() {
        val nearCells = NearCells()

        nearCells.above = MapCell()
        nearCells.below = MapCell()
        nearCells.left = MapCell()
        nearCells.right = MapCell()

        assertNotNull(nearCells.above)
        assertNotNull(nearCells.below)
        assertNotNull(nearCells.left)
        assertNotNull(nearCells.right)
    }

}
