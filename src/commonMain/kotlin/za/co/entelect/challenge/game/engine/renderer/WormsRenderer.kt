package za.co.entelect.challenge.game.engine.renderer

import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.renderer.printables.PrintableMapCell

interface WormsRenderer {

    fun commandPrompt(wormsPlayer: WormsPlayer): String

    fun render(wormsMap: WormsMap, player: WormsPlayer): String

    val EOL: String
        get() = "\n"

    fun getStringMap(arrayMap: List<List<PrintableMapCell>>): String {
        return arrayMap.joinToString(EOL) {
            it.joinToString("") { cell ->
                when {
                    cell.powerup != null -> cell.powerup.printable
                    cell.occupier != null -> cell.occupier?.printable.toString()
                    else -> cell.type.printable
                }
            }
        }
    }

}
