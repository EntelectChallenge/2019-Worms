package za.co.entelect.challenge.game.engine.map

import za.co.entelect.challenge.game.engine.interfaces.Printable

/**
 * Defines the type of a cell
 * @param diggable Defines if a worm can be dig through cells of this type
 * @param open Defines if a worm move into or shoot trough cells of this type
 */
enum class CellType(val diggable: Boolean, val open: Boolean, override val printable: String) : Printable {

    AIR(false, true, "░░"),
    DIRT(true, false, "▓▓"),
    DEEP_SPACE(false, false, "██")

}
