package za.co.entelect.challenge.game.engine.command

import org.junit.Assert
import za.co.entelect.challenge.game.contracts.command.RawCommand
import za.co.entelect.challenge.game.contracts.game.GamePlayer
import za.co.entelect.challenge.game.contracts.map.GameMap
import za.co.entelect.challenge.game.contracts.player.Player
import za.co.entelect.challenge.game.contracts.renderer.RendererType
import za.co.entelect.challenge.game.delegate.bootstrapper.WormsGameBoostrapper
import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG_PATH
import za.co.entelect.challenge.game.delegate.player.DelegatePlayer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val bootstrapper = WormsGameBoostrapper().apply {
    setSeed(0)
    setConfigPath(TEST_CONFIG_PATH)
}

private class MockPlayer(name: String?, number: Int) : Player(name) {

    init {
        setNumber(number)
    }

    override fun startGame(gameMap: GameMap?) {
        throw NotImplementedError()
    }

    override fun newRoundStarted(gameMap: GameMap?) {
        throw NotImplementedError()
    }

    override fun gameEnded(gameMap: GameMap?) {
        throw NotImplementedError()
    }

}

class BananaCommandIntegrationTest {

    @Test
    fun test_bananaCommands() {
        val players: List<Player> = listOf(
                MockPlayer("Player 1", 1),
                MockPlayer("Player 2", 2)
        )

        val mapGenerator = bootstrapper.mapGenerator
        val gameMap = mapGenerator.generateGameMap(players)

        val roundProcessor = bootstrapper.roundProcessor
        roundProcessor.processRound(gameMap, mapOf(
                players[0].gamePlayer to listOf(RawCommand("${CommandStrings.SELECT.string} 3"), RawCommand("${CommandStrings.BANANA.string} 24 5")),
                players[1].gamePlayer to listOf(RawCommand("${CommandStrings.MOVE.string} 30 17"))
        ))

        val renderer = bootstrapper.getRenderer(rendererType = RendererType.TEXT)
        val renderPlayer1 = renderer.render(gameMap, players[0].gamePlayer)
        val renderPlayer2 = renderer.render(gameMap, players[1].gamePlayer)

        assertTrue(gameMap.refereeIssues.isValid)

        assertTextRenderedBananaCount(renderPlayer1, players[0].gamePlayer, 2)
        assertTextRenderedBananaCount(renderPlayer2, players[1].gamePlayer, 3)
    }

    private fun assertTextRenderedBananaCount(content: String, player: GamePlayer, expected: Int) {
        assertTrue(player is DelegatePlayer)
        val wormsPlayer = player.wormsPlayer

        assertEquals(expected, wormsPlayer.worms[2].bananas?.count, "Player ${wormsPlayer.id} banana count correct")

        val pattern = "Banana bombs count: (\\d)".toRegex()

        val matchResult = pattern.findAll(content).toList()

        Assert.assertEquals(1, matchResult.size)

        val (count) = matchResult[0].destructured
        assertEquals(expected, count.toInt(), "Player ${wormsPlayer.id} banana count rendered correct")
    }

}
