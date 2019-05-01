package za.co.entelect.challenge.game.engine.command

import com.nhaarman.mockitokotlin2.*
import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.command.feedback.StandardCommandFeedback
import za.co.entelect.challenge.game.engine.command.feedback.CommandValidation
import za.co.entelect.challenge.game.engine.factory.TestMapFactory.buildMapWithCellType
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandExecutorTest {

    val config = TEST_CONFIG
    val worms = CommandoWorm.build(0, config, Point(1, 1))
    val player = WormsPlayer.build(1, listOf(worms), config)
    val mockMap = buildMapWithCellType(listOf(player), 4, CellType.AIR)

    @Test
    fun test_invalidMove() {
        val command: WormsCommand = mock {
            on { validate(any(), any()) }.doReturn(CommandValidation.invalidMove("Testing"))
        }

        val executor = CommandExecutor(player, mockMap, command, config)

        executor.execute()
        assertEquals(1, player.consecutiveDoNothingsCount)
        assertEquals(config.scores.invalidCommand, player.commandScore)

        executor.execute()
        assertEquals(2, player.consecutiveDoNothingsCount)
        assertEquals(config.scores.invalidCommand * 2, player.commandScore)

        verify(command, times(0)).execute(any(), any())
    }

    @Test
    fun test_validMove() {
        val validCommand: WormsCommand = mock {
            on { validate(any(), any()) }.doReturn(CommandValidation.validMove())
            on { execute(any(), any()) }.doReturn(StandardCommandFeedback("nothing", 10, 1))
        }

        val executor = CommandExecutor(player, mockMap, validCommand, config)

        executor.execute()
        assertEquals(0, player.consecutiveDoNothingsCount)
        assertEquals(10, player.commandScore)

        executor.execute()
        assertEquals(0, player.consecutiveDoNothingsCount)
        assertEquals(20, player.commandScore)

        verify(validCommand, times(2)).execute(any(), any())
    }
}
