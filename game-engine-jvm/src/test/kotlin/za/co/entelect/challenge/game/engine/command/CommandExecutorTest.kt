package za.co.entelect.challenge.game.engine.command

import com.nhaarman.mockitokotlin2.*
import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
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
    val mockMap = TestMapFactory.buildMapWithCellType(listOf(player), 4, CellType.AIR)

    @Test
    fun test_invalidMove() {
        val command: WormsCommand = mock {
            on { validate(any(), any()) }.doReturn(CommandValidation.invalidMove("Testing"))
        }

        val executor = CommandExecutor(player, mockMap, command)

        executor.execute()
        assertEquals(1, player.consecutiveDoNothingsCount)

        executor.execute()
        assertEquals(2, player.consecutiveDoNothingsCount)

        verify(command, times(0)).execute(any(), any())
    }

    @Test
    fun test_validMove() {
        val validCommand: WormsCommand = mock {
            on { validate(any(), any()) }.doReturn(CommandValidation.validMove())
        }

        val executor = CommandExecutor(player, mockMap, validCommand)

        executor.execute()
        assertEquals(0, player.consecutiveDoNothingsCount)

        executor.execute()
        assertEquals(0, player.consecutiveDoNothingsCount)

        verify(validCommand, times(2)).execute(any(), any())
    }
}