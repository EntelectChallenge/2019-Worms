package za.co.entelect.challenge.game.engine.renderer.printables

import za.co.entelect.challenge.game.engine.map.Point

class PrintableVisualizerEvent(visualizerEvent: VisualizerEvent) {

    val type: String = visualizerEvent.type
    val result: String? = visualizerEvent.result
    val positionStart: Point? = visualizerEvent.positionStart
    val positionEnd: Point? = visualizerEvent.positionEnd
    val wormCommanded: PrintableWorm = PrintableWorm.buildForVisualizerEvent(visualizerEvent.wormCommanded)
    val affectedCells: List<PrintableMapCell>? = visualizerEvent.affectedCells?.map { PrintableMapCell(it) }

}
