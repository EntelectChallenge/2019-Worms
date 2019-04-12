package za.co.entelect.challenge.game.delegate.engine

import za.co.entelect.challenge.game.contracts.game.GameMapGenerator
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.contracts.player.Player
import za.co.entelect.challenge.game.delegate.player.DelegatePlayer
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMapGenerator
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class DelegateMapGenerator(private val config: GameConfig, seed: Int) : GameMapGenerator {

    private val wormsMapGenerator = WormsMapGenerator(config, seed)

    override fun generateGameMap(players: List<Player>): GameMap {
        val wormsPlayers = mutableListOf<WormsPlayer>()

        //The runner does not populate the player number as originally expected, so we need to override them
        overridePlayerIds(players)

        players.forEach { player ->
            val wormsPlayer = WormsPlayer.build(player.number, config)
            player.gamePlayer = DelegatePlayer(wormsPlayer)
            wormsPlayers.add(wormsPlayer)
        }

        val wormsMap = wormsMapGenerator.getMap(wormsPlayers)
        return DelegateMap(wormsMap)
    }

    private fun overridePlayerIds(players: List<Player>) {
        players.forEachIndexed { i, player ->
            player.number = i + 1
        }
    }
}
