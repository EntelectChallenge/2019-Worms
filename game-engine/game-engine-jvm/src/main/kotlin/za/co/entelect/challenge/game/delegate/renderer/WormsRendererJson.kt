package za.co.entelect.challenge.game.delegate.renderer

import com.google.gson.Gson
import za.co.entelect.challenge.game.contracts.game.GamePlayer
import za.co.entelect.challenge.game.contracts.renderer.RendererType
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.powerups.HealthPack
import java.lang.Exception

class WormsRendererJson(private val config: GameConfig) : WormsRenderer {

    private val gson = Gson()

    override fun commandPrompt(wormsPlayer: WormsPlayer): String {
        return "Not supported in JSON state file"
    }

    override fun render(wormsMap: WormsMap, player: WormsPlayer): String {
        val wormGameDetails = WormsGameDetails(config, wormsMap, player)

        return gson.toJson(wormGameDetails)
    }

}
