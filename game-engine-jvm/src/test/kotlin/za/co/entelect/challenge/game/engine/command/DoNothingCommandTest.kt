package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.entities.GameConfig
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DoNothingCommandTest {

    val config = GameConfig()
    val worm = CommandoWorm.build(config, Point(0,0))
    val player = WormsPlayer(0, listOf(worm))
    val map = TestMapFactory.buildMapWithCellType(listOf(player), 4, 4, CellType.AIR)

    @Test
    fun testDoNothing() {
        val command = DoNothingCommand()
        assertTrue(command.validate(map, player.currentWorm).isValid)
        command.execute(map, player.currentWorm)

        assertEquals(1, player.doNothingsCount)
    }
}