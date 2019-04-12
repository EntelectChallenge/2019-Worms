package za.co.entelect.challenge.game.engine.map

class NearCells(var above: MapCell? = null,
                var below: MapCell? = null,
                var left: MapCell? = null,
                var right: MapCell? = null,
                var aboveLeft: MapCell? = null,
                var belowLeft: MapCell? = null,
                var aboveRight: MapCell? = null,
                var belowRight: MapCell? = null) {

    /**
     * Returns an array of existing neighbouring cells in cardinal directions (Plus shaped)
     * @returns {MapCell[]}
     */
    fun getCardinalList(): List<MapCell> = listOfNotNull(above, below, left, right)

    /**
     * Returns an array of existing neighbouring cells in ordinal directions (X shaped)
     * @returns {MapCell[]}
     */
    fun getOrdinalList(): List<MapCell> = listOfNotNull(aboveLeft, belowLeft, aboveRight, belowRight)

    /**
     * Returns an array all existing neighbouring cells
     * @returns {MapCell[]}
     */
    fun getAllCells(): List<MapCell> = listOf(getCardinalList(), getOrdinalList()).flatten()

    fun setAllNearCells(blankMap: List<List<MapCell>>, centerCell: MapCell) {
        above = blankMap.getOrNull(centerCell.x)?.getOrNull(centerCell.y - 1)
        below = blankMap.getOrNull(centerCell.x)?.getOrNull(centerCell.y + 1)
        left = blankMap.getOrNull(centerCell.x - 1)?.getOrNull(centerCell.y)
        right = blankMap.getOrNull(centerCell.x + 1)?.getOrNull(centerCell.y)

        aboveLeft = blankMap.getOrNull(centerCell.x - 1)?.getOrNull(centerCell.y - 1)
        belowLeft = blankMap.getOrNull(centerCell.x - 1)?.getOrNull(centerCell.y + 1)
        aboveRight = blankMap.getOrNull(centerCell.x + 1)?.getOrNull(centerCell.y - 1)
        belowRight = blankMap.getOrNull(centerCell.x + 1)?.getOrNull(centerCell.y + 1)
    }

}
