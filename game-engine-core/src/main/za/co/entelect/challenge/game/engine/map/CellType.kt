package za.co.entelect.challenge.game.engine.map

enum class CellType(val diggable: Boolean, val open: Boolean) {

    AIR(false, true),
    DIRT(true, false),
    BEDROCK(false, false)

}