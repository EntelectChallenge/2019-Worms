package za.co.entelect.challenge.game.delegate.renderer

import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer

interface WormsRenderer {

    fun commandPrompt(player: WormsPlayer): String

    fun render(wormsMap: WormsMap, player: WormsPlayer): String

}
