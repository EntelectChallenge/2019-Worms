package za.co.entelect.challenge.game.delegate.renderer

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
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
    fun test_single_last_move_added_correctly() {
        val wormsPlayers = buildWormsPlayerDefault(config)
        wormsPlayers.forEachIndexed { i, p ->
            p.worms.forEachIndexed { j, w ->
                w.initPositions(Point(i, j))
            }
        }
        val player1 = wormsPlayers.first()
        val player2 = wormsPlayers.get(1)

        val lightPixel = CellType.AIR
        val wormsMap = buildMapWithCellType(wormsPlayers, config.mapSize, lightPixel)

        wormsMap.addFeedback(StandardCommandFeedback("move 2 3", 5, player1.id, true))
        wormsMap.addFeedback(StandardCommandFeedback("select 2", 5, player2.id, true))
        wormsMap.addFeedback(StandardCommandFeedback("move 20 3", 5, player2.id, true))

        wormsMap.currentRound++

        val gameDetailsForPlayer1 = WormsGameDetails(config, wormsMap, player1)
        val gameDetailsForPlayer2 = WormsGameDetails(config, wormsMap, player2)

        assertEquals(gameDetailsForPlayer1.opponentsLastCommand, "select 2; move 20 3")
        assertEquals(gameDetailsForPlayer2.opponentsLastCommand, "move 2 3")
    }
}
