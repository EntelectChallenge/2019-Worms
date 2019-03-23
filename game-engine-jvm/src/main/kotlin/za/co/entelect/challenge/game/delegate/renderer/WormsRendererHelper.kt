package za.co.entelect.challenge.game.delegate.renderer

import za.co.entelect.challenge.game.engine.map.MapCell

object WormsRendererHelper {

    private val EOL = System.getProperty("line.separator")

    fun getStringMap(arrayMap: List<List<MapCell>>): String {
        return arrayMap.joinToString(EOL) {
            it.joinToString("") { cell ->
                when {
                    cell.powerup != null -> cell.powerup?.printable.toString()
                    cell.occupier != null -> cell.occupier?.printable.toString()
                    else -> cell.type.printable
                }
            }
        }
    }

}
