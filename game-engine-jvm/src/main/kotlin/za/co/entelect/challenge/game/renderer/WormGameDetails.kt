package za.co.entelect.challenge.game.renderer

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class WormGameDetails(config: GameConfig,
                      wormsMap: WormsMap,
                      cells: List<List<MapCell>>,
                      player: WormsPlayer) {

    var currentRound: Int = wormsMap.currentRound
    var maxRounds: Int = config.maxRounds
    var mapSize: Int = config.mapSize
    var selfPlayer: WormsPlayer = player
    var players: List<WormsPlayer> = wormsMap.players
    var map: List<List<MapCell>> = cells

}
