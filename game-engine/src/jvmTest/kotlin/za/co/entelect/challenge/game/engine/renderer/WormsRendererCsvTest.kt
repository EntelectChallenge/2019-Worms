package za.co.entelect.challenge.game.engine.renderer

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.command.CommandStrings
import za.co.entelect.challenge.game.engine.command.feedback.StandardCommandFeedback
import za.co.entelect.challenge.game.engine.factory.TestMapFactory
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.Test
import kotlin.test.assertEquals

class WormsRendererCsvTest {

    val config = TEST_CONFIG

    private val round1Expected =
            """|Round,LastCommandType,LastCommand,ActiveWorm,Score,Health,Worm1 Health,Worm1 x,Worm1 y,Worm2 Health,Worm2 x,Worm2 y,Worm3 Health,Worm3 x,Worm3 y
               |1,null,"null",1,35,60,10,0,1,20,2,3,30,4,5""".trimMargin()

    private val round2Expected = """2,move,"move 22 28",1,35,60,10,0,1,20,2,3,30,4,5"""

    private val renderer = WormsRendererCsv(config)

    @Test
    fun testHeader() {
        val (player, map) = setupMap()
        map.currentRound = 1


        assertEquals(round1Expected, renderer.render(map, player))
    }

    private fun setupMap(): Pair<WormsPlayer, WormsMap> {
        val player = WormsPlayer.build(1, config)
        player.commandScore += 15

        player.worms[0].initPositions(Point(0, 1))
        player.worms[0].health = 10
        player.worms[1].initPositions(Point(2, 3))
        player.worms[1].health = 20
        player.worms[2].initPositions(Point(4, 5))
        player.worms[2].health = 30

        val map = TestMapFactory.buildMapWithCellType(listOf(player), 36, CellType.AIR)
        return Pair(player, map)
    }

    @Test
    fun testCommand() {
        val (player, map) = setupMap()
        map.currentRound = 1
        map.addFeedback(StandardCommandFeedback(command = "${CommandStrings.SELECT.string} 1", score = 0, playerId = player.id))
        map.addFeedback(StandardCommandFeedback(command = "${CommandStrings.MOVE.string} 22 28", score = 0, playerId = player.id))

        //Renderers get called at the beginning of a round
        map.currentRound = 2
        assertEquals(round2Expected, renderer.render(map, player))
    }
}
