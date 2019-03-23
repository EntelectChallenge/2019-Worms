package za.co.entelect.challenge.game.engine.command.implementation

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.command.CommandExecutor
import za.co.entelect.challenge.game.engine.command.TestMapFactory
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DoNothingCommandTest {

    val config = TEST_CONFIG
    val worm = CommandoWorm.build(0, config, Point(0, 0))
    val player = WormsPlayer.build(0, listOf(worm), config)
    val map = TestMapFactory.buildMapWithCellType(listOf(player), 4, CellType.AIR)

    @Test
    fun testDoNothing() {
        val command = DoNothingCommand(config)
        val commandExecutor = CommandExecutor(player, map, command, config)

        assertTrue(command.validate(map, player.currentWorm).isValid)
        commandExecutor.execute()

        assertEquals(1, player.consecutiveDoNothingsCount)

        commandExecutor.execute()
        assertEquals(2, player.consecutiveDoNothingsCount)
    }
}