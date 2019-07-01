package za.co.entelect.challenge.game.engine.command.implementation

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.factory.TestMapFactory
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SelectCommandTest {

    private val config: GameConfig = TEST_CONFIG
    private val map = TestMapFactory.buildMapWithCellType(emptyList(), 4, CellType.AIR)

    @Before
    fun init() {
    }

    @Test
    fun validate_unknownWorm() {
        val player = setupPlayer()
        val worms = player.worms

        val command = SelectCommand(10)
        assertFalse(command.validate(map, worms[0]).isValid)

        command.execute(map, worms[0])
        assertEquals(worms[0], player.currentWorm)
    }

    private fun setupPlayer(): WormsPlayer {
        val worms = listOf(CommandoWorm.build(0, config, Point(1, 1)),
                CommandoWorm.build(1, config, Point(1, 2)))
        return WormsPlayer.build(0, worms, config)
    }

    @Test
    fun validate_deadWorm() {
        val player = setupPlayer()
        val worms = player.worms
        worms[0].health = 0

        val command = SelectCommand(0)

        assertFalse(command.validate(map, worms[0]).isValid)

        command.execute(map, worms[0])
        assertEquals(worms[0], player.currentWorm)
    }

    @Test
    fun validate_noTokens() {
        val player = setupPlayer()
        val worms = player.worms

        player.wormSelectionTokens = 0
        val command = SelectCommand(1)

        assertFalse(command.validate(map, worms[0]).isValid)
    }

    @Test
    fun valid() {
        val player = setupPlayer()
        val worms = player.worms

        val command = SelectCommand(1)

        assertTrue(command.validate(map, worms[0]).isValid)

        command.execute(map, worms[0])

        assertEquals(worms[1], player.currentWorm)
        assertEquals(0, player.wormSelectionTokens)
    }


}
