package za.co.entelect.challenge.game.delegate

import za.co.entelect.challenge.game.contracts.renderer.RendererType
import za.co.entelect.challenge.game.delegate.factory.GameConfigFactory
import za.co.entelect.challenge.game.engine.command.implementation.DigCommand
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.WormsMapGenerator
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.processor.WormsRoundProcessor
import za.co.entelect.challenge.game.renderer.WormsRenderer

/**
 * Get 2 players with 3 worms each
 */
private fun getPlayers2Worms3(config: GameConfig) = (1..3).map {
    val playerSquad = (1..3).map { wormIndex ->
        CommandoWorm.build(wormIndex, config)
    }

    WormsPlayer.build(it, playerSquad, config)
}
        .toMutableList()

fun main(args: Array<String>) {
    println("Hello, World!")

    val wormsRoundProcessor = WormsRoundProcessor()

    val TEST_CONFIG_PATH = "game-engine-jvm/src/main/resources/default-config.json"
    val config = GameConfigFactory.getConfig(TEST_CONFIG_PATH)

    val wormsMapGenerator = WormsMapGenerator(config, 0)

    val players2Worms3 = getPlayers2Worms3(config)
    val wormsMap = wormsMapGenerator.getMap(players2Worms3)

    val player1 = players2Worms3[0]
    val player2 = players2Worms3[1]
    val command = DigCommand(1, 1)

    val commandMap = mapOf(Pair(player1, command), Pair(player2, command))
    wormsRoundProcessor.processRound(wormsMap, commandMap)

    val errorList = wormsRoundProcessor.getErrorList(wormsMap)

    val rendererText = WormsRenderer(config, RendererType.TEXT)
    val rendererJson = WormsRenderer(config, RendererType.JSON)
    val rendererConsole = WormsRenderer(config, RendererType.CONSOLE)

    val textFile = rendererText.render(wormsMap, player1)
    val jsonFile = rendererJson.render(wormsMap, player1)
    val consoleFile = rendererConsole.render(wormsMap, player1)

    println("Bye bye, World!")
}
