package za.co.entelect.challenge.game.engine.map

enum class CellType(val diggable: Boolean, val movable: Boolean) {

    AIR(false, true),
    DIRT(true, false),
    DEEP_SPACE(false, false)

}
