import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.map.WormsMapGenerator
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class GameRunner(val seed: Int, val config: GameConfig, val playerCount: Int = 2) {

    fun generateMap(): WormsMap {
        val players = (0..playerCount).map {
            WormsPlayer.build(it, config)
        }

        return WormsMapGenerator(config, seed).getMap(players)
    }
}