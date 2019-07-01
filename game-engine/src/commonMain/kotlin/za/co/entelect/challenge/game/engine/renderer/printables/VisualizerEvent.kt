package za.co.entelect.challenge.game.engine.renderer.printables

import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.Worm

class VisualizerEvent(val type: String,
                      val result: String?,
                      _wormCommanded: Worm,
                      val positionStart: Point?,
                      val positionEnd: Point?) {
    val wormCommanded: PrintableWorm = PrintableWorm.buildForVisualizerEvent(_wormCommanded)
}
