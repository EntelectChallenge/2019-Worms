package za.co.entelect.challenge.game.engine.renderer

import za.co.entelect.challenge.game.delegate.json.JsonSerializer
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class WormsRendererJson(private val config: GameConfig) : WormsRenderer {

    private val json = JsonSerializer()

    override fun commandPrompt(wormsPlayer: WormsPlayer): String {
        return "Not supported in JSON state file"
    }

    override fun render(wormsMap: WormsMap, player: WormsPlayer): String {
        val wormGameDetails = WormsGameDetails(config, wormsMap, player)

        return json.toJson(wormGameDetails)
    }

}
