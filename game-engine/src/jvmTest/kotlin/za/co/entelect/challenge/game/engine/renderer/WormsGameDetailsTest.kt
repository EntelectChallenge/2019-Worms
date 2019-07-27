package za.co.entelect.challenge.game.delegate.renderer

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.command.CommandStrings
import za.co.entelect.challenge.game.engine.command.feedback.StandardCommandFeedback
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.factory.TestMapFactory.buildMapWithCellType
import za.co.entelect.challenge.game.engine.factory.TestWormsPlayerFactory.buildWormsPlayerDefault
import za.co.entelect.challenge.game.engine.factory.TestWormsPlayerFactory.buildWormsPlayers
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.renderer.WormsGameDetails
import kotlin.test.Test
import kotlin.test.assertEquals

class WormsGameDetailsTest {

    private val config: GameConfig = TEST_CONFIG

    @Test
    fun test_last_move_added_correctly() {
        val wormsPlayers = buildWormsPlayerDefault(config)
        wormsPlayers.forEachIndexed { i, p ->
            p.worms.forEachIndexed { j, w ->
                w.initPositions(Point(i, j))
            }
        }
        val player1 = wormsPlayers.first()
        val player2 = wormsPlayers[1]

        val lightPixel = CellType.AIR
        val wormsMap = buildMapWithCellType(wormsPlayers, config.mapSize, lightPixel)

        wormsMap.addFeedback(StandardCommandFeedback("${CommandStrings.MOVE.string} 2 3", 5, player1.id, true))
        wormsMap.addFeedback(StandardCommandFeedback("${CommandStrings.SELECT.string} 2", 5, player2.id, true))
        wormsMap.addFeedback(StandardCommandFeedback("${CommandStrings.MOVE.string} 20 3", 5, player2.id, true))

        wormsMap.currentRound++

        val gameDetailsForPlayer1 = WormsGameDetails(config, wormsMap, player1)
        val gameDetailsForPlayer2 = WormsGameDetails(config, wormsMap, player2)

        assertEquals("select 2; move 20 3", gameDetailsForPlayer1.opponents[0].previousCommand)
        assertEquals("move 2 3", gameDetailsForPlayer2.opponents[0].previousCommand)
    }
}
