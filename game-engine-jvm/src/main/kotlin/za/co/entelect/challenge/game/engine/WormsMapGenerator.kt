package za.co.entelect.challenge.game.engine

import za.co.entelect.challenge.game.contracts.game.GameMapGenerator
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.contracts.player.Player
import za.co.entelect.challenge.game.entities.WormsMap

class WormsMapGenerator : GameMapGenerator {

    override fun generateGameMap(players: List<Player>): GameMap {

        val gameMap = WormsMap()

        //TODO

        return gameMap
    }
}
