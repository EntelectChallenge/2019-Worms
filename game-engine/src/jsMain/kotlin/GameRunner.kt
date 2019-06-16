import za.co.entelect.challenge.game.engine.WormsEngine
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.factory.CommandParser
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.map.WormsMapGenerator
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.processor.GameError
import za.co.entelect.challenge.game.engine.processor.WormsRoundProcessor
import kotlin.random.Random

class GameRunner(val seed: Int, val config: GameConfig, val playerCount: Int = 2) {

    @JsName("getGeneratedMap")
    fun getGeneratedMap(): WormsMap {
        val players = (1..playerCount).map {
            WormsPlayer.build(it, config)
        }

        return WormsMapGenerator(config, seed).getMap(players)
    }

    @JsName("isGameComplete")
    fun isGameComplete(wormsMap: WormsMap): Boolean {
        return WormsEngine(config).isGameComplete(wormsMap)
    }

    @JsName("processRound")
    fun processRound(wormsMap: WormsMap,
                     player1: WormsPlayer, player1Command: String,
                     player2: WormsPlayer, player2Command: String): Boolean {
        val parser = CommandParser(Random.Default, config)

        val wormsCommands = mapOf(Pair(player1, listOf(parser.parseCommand(player1Command))),
                Pair(player2, listOf(parser.parseCommand(player2Command))))

        return WormsRoundProcessor(config).processRound(wormsMap, wormsCommands)
    }

    @JsName("getErrorList")
    fun getErrorList(wormsMap: WormsMap, wormsPlayer: WormsPlayer): List<GameError> {
        return WormsRoundProcessor(config).getErrorList(wormsMap, wormsPlayer)
    }

    @JsName("getAllErrorList")
    fun getAllErrorList(wormsMap: WormsMap): List<GameError> {
        return WormsRoundProcessor(config).getErrorList(wormsMap)
    }

}
