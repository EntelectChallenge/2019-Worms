package za.co.entelect.challenge.game.engine.command

import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import za.co.entelect.challenge.game.engine.entities.GameConfig
import za.co.entelect.challenge.game.engine.entities.MoveValidation
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.assertEquals

class CommandExecutorTest {

    val config = GameConfig()
    val worms = CommandoWorm.build(config, Point(1, 1))
    val player = WormsPlayer(1, listOf(worms))
    val mockMap = TestMapFactory.buildMapWithCellType(listOf(player), 4, 4, CellType.AIR)

    @Test
    fun test_invalidMove() {
        val command: WormsCommand = mock {
            on { validate(any(), any()) }.doReturn(MoveValidation.invalidMove("Testing"))
        }

        val executor = CommandExecutor(player, mockMap, command)

        executor.execute()
        assertEquals(1, player.doNothingsCount)

        executor.execute()
        assertEquals(2, player.doNothingsCount)

        verify(command, times(0)).execute(any(), any())
    }

    @Test
    fun test_validMove() {
        val validCommand: WormsCommand = mock {
            on { validate(any(), any()) }.doReturn(MoveValidation.validMove())
        }

        val executor = CommandExecutor(player, mockMap, validCommand)

        executor.execute()
        assertEquals(0, player.doNothingsCount)

        executor.execute()
        assertEquals(0, player.doNothingsCount)

        verify(validCommand, times(2)).execute(any(), any())
    }
}