package za.co.entelect.challenge.game.delegate.engine

import za.co.entelect.challenge.game.contracts.game.GameMapGenerator
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.contracts.player.Player
import za.co.entelect.challenge.game.delegate.player.DelegatePlayer
import za.co.entelect.challenge.game.engine.entities.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMapGenerator
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class DelegateMapGenerator(private val config: GameConfig, private val seed: Long) : GameMapGenerator {

    private val wormsMapGenerator = WormsMapGenerator(config, seed)

    override fun generateGameMap(players: List<Player>): GameMap {
        val wormsPlayers = mutableListOf<WormsPlayer>()

        players.forEachIndexed { index, player ->
            val wormsPlayer = WormsPlayer.build(player.number, config)
            player.gamePlayer = DelegatePlayer(wormsPlayer)
            wormsPlayers.add(wormsPlayer)
        }

        val wormsMap = wormsMapGenerator.getMap(wormsPlayers)
        return DelegateMap(wormsMap)
    }
}
