package za.co.entelect.challenge.game.engine

import za.co.entelect.challenge.game.contracts.game.GameMapGenerator
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.contracts.player.Player
import za.co.entelect.challenge.game.entities.WormsWrapperMap

class WrapperWrapperMapGenerator : GameMapGenerator {

    override fun generateGameMap(players: List<out Player>): GameMap {

        val gameMap = WormsWrapperMap(players)

        //TODO

        return gameMap
    }
}
