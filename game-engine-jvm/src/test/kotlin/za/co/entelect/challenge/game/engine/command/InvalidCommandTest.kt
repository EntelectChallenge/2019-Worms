package za.co.entelect.challenge.game.engine.command

import za.co.entelect.challenge.game.engine.entities.GameConfig
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import kotlin.test.Test
import kotlin.test.assertFalse

class InvalidCommandTest {

    val worm = CommandoWorm.build(GameConfig())
    val map = TestMapFactory.buildMapWithCellType(emptyList(),4,4, CellType.AIR)

    @Test
    fun test_validation() {
        val command = InvalidCommand("Cannot parse command")
        assertFalse(command.validate(map, worm).isValid)
    }

    @Test(expected = NotImplementedError::class)
    fun testExecution() {
        val command = InvalidCommand("Cannot parse command")
        command.execute(map, worm)
    }
}