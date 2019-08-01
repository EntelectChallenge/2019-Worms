package za.co.entelect.challenge.game.engine.renderer

import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer

interface WormsRenderer {

    fun commandPrompt(wormsPlayer: WormsPlayer): String

    fun render(wormsMap: WormsMap, player: WormsPlayer?): String

    companion object {
        const val EOL: String = "\n"
    }
}
