import za.co.entelect.challenge.game.engine.WormsEngine
import za.co.entelect.challenge.game.engine.command.feedback.CommandFeedback
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.factory.CommandParser
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.map.WormsMapGenerator
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.processor.GameError
import za.co.entelect.challenge.game.engine.processor.WormsRoundProcessor
import za.co.entelect.challenge.game.engine.renderer.WormsRendererJson
import kotlin.random.Random

class GameRunner(val seed: Int, val config: GameConfig, val playerCount: Int = 2) {

    private val rendererJson = WormsRendererJson(config)

    @JsName("getGeneratedMap")
    fun getGeneratedMap(): WormsMap {
        val players = (1..playerCount).map {
            WormsPlayer.build(it, config)
        }

        return WormsMapGenerator(config, seed).getMap(players)
    }

    @JsName("isGameComplete")
    fun isGameComplete(wormsMap: WormsMap) = WormsEngine(config).isGameComplete(wormsMap)

    @JsName("processRound")
    fun processRound(wormsMap: WormsMap, vararg commandList: Pair<WormsPlayer, String>): Boolean {
        val parser = CommandParser(Random.Default, config)

        val wormsCommands = commandList.groupBy { it.first }
                .mapValues { (_, values) -> values.map { parser.parseCommand(it.second) } }

        return WormsRoundProcessor(config).processRound(wormsMap, wormsCommands)
    }

    @JsName("renderJson")
    fun renderJson(map: WormsMap, player: WormsPlayer) = rendererJson.render(map, player)

    @JsName("getErrorList")
    fun getErrorList(wormsMap: WormsMap, wormsPlayer: WormsPlayer): List<GameError> {
        return WormsRoundProcessor(config).getErrorList(wormsMap, wormsPlayer)
    }

    @JsName("getAllErrorList")
    fun getAllErrorList(wormsMap: WormsMap) = WormsRoundProcessor(config).getErrorList(wormsMap)

    @JsName("getFeedback")
    fun getFeedback(wormsMap: WormsMap) = wormsMap.getFeedback(wormsMap.currentRound)

    @JsName("getAllFeedback")
    fun getAllFeedback(wormsMap: WormsMap) = wormsMap.getAllFeedback().map { it.value }

    @JsName("setCurrentRound")
    fun setCurrentRound(wormsMap: WormsMap, newValue: Int) {
        wormsMap.currentRound = newValue
    }

}
