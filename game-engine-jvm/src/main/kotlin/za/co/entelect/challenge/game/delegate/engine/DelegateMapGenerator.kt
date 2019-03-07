package za.co.entelect.challenge.game.delegate.engine

import za.co.entelect.challenge.game.delegate.factory.GameConfigFactory
import za.co.entelect.challenge.game.contracts.game.GameMapGenerator
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.contracts.player.Player
import za.co.entelect.challenge.game.delegate.player.DelegatePlayer
import za.co.entelect.challenge.game.engine.map.WormsMapGenerator
import za.co.entelect.challenge.game.engine.player.WormsPlayer

class DelegateMapGenerator : GameMapGenerator {

    private val config = GameConfigFactory.getConfig()
    private val wormsMapGenerator = WormsMapGenerator(config)

    override fun generateGameMap(players: MutableList<Player>): GameMap {
        val wormsPlayers = mutableListOf<WormsPlayer>()

        players.forEach{
            val wormsPlayer = WormsPlayer.construct(config)
            it.gamePlayer = DelegatePlayer(wormsPlayer)
            wormsPlayers.add(wormsPlayer)
        }

        val wormsMap = wormsMapGenerator.generateMap(wormsPlayers)
        return DelegateMap(wormsMap)
    }
}
