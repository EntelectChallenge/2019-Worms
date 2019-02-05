package za.co.entelect.challenge.game.engine

import za.co.entelect.challenge.game.contracts.exceptions.TimeoutException
import za.co.entelect.challenge.game.contracts.game.GameEngine
import za.co.entelect.challenge.game.contracts.map.GameMap

class WormsWrapperEngine : GameEngine {

    override fun isGameComplete(gameMap: GameMap): Boolean {
//        if (gameMap !is Wr) {
//            throw  IllegalArgumentException("Invalid map class")
//        }
//
//        if (!playersInValidState(gameMap)) {
//            throw TimeoutException("Too many do nothing commands received due to exceptions")
//        }
//
//        return gameMap.currentRound > GameConfig.maxRounds || gameMap.livingPlayers.size <= 1
        return true
    }

    private fun playersInValidState(gameMap: GameMap): Boolean {
//        val players = gameMap.players
//        for (player in players) {
//            if (player.consecutiveDoNothings >= GameConfig.maxDoNothings) {
//                return false
//            }
//        }
//
        return true
    }
}
