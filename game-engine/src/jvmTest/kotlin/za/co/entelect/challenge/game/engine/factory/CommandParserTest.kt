package za.co.entelect.challenge.game.engine.factory

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.command.CommandStrings
import za.co.entelect.challenge.game.engine.command.implementation.*
import za.co.entelect.challenge.game.engine.map.Point
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommandParserTest {

    private val parser = CommandParser(Random.Default, TEST_CONFIG)

    @Test
    fun move_valid() {
        val command = parser.parseCommand("${CommandStrings.MOVE.string} 3 4")
        assertTrue(command is TeleportCommand)
        assertEquals(Point(3, 4), command.target)
    }

    @Test
    fun move_invalidNumber_x() {
        val command = parser.parseCommand("${CommandStrings.MOVE.string} 3 a")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun move_invalidNumber_y() {
        val command = parser.parseCommand("${CommandStrings.MOVE.string} c 2")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun move_invalidNumber_both() {
        val command = parser.parseCommand("${CommandStrings.MOVE.string} e f")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun move_tooShort() {
        val command = parser.parseCommand("${CommandStrings.MOVE.string} 3")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun dig_valid() {
        val command = parser.parseCommand("${CommandStrings.DIG.string} 2 1")
        assertTrue(command is DigCommand)
        assertEquals(Point(2, 1), command.target)
    }

    @Test
    fun dig_invalidNumber_x() {
        val command = parser.parseCommand("${CommandStrings.DIG.string} b 4")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun dig_invalidNumber_y() {
        val command = parser.parseCommand("${CommandStrings.DIG.string} 3 a")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun dig_invalidNumber_both() {
        val command = parser.parseCommand("${CommandStrings.DIG.string} x y")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun dig_tooShort() {
        val command = parser.parseCommand("${CommandStrings.DIG.string}")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun shoot_valid() {
        val command = parser.parseCommand("${CommandStrings.SHOOT.string} NW")
        assertTrue(command is ShootCommand)
        assertEquals(Direction.UP_LEFT, command.direction)
    }

    @Test
    fun shoot_invalid() {
        val command = parser.parseCommand("${CommandStrings.SHOOT.string} TEST")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun shoot_tooShort() {
        val command = parser.parseCommand("${CommandStrings.SHOOT.string}")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun select_valid() {
        val command = parser.parseCommand("${CommandStrings.SELECT.string} 1")
        assertTrue(command is SelectCommand)
        assertEquals(1, command.wormId)
    }

    @Test
    fun select_invalid() {
        val command = parser.parseCommand("${CommandStrings.SELECT.string} A")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun select_tooShort() {
        val command = parser.parseCommand("${CommandStrings.SELECT.string}")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun banana_valid() {
        val command = parser.parseCommand("${CommandStrings.BANANA.string} 15 13")
        assertTrue(command is BananaCommand)
        assertEquals(Point(15, 13), command.target)
    }

    @Test
    fun banana_invalid() {
        val command = parser.parseCommand("${CommandStrings.BANANA.string} A 5")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun banana_tooShort() {
        val commandOnly = parser.parseCommand("${CommandStrings.BANANA.string}")
        assertTrue(commandOnly is InvalidCommand)

        val commandOneParameter = parser.parseCommand("${CommandStrings.BANANA.string} 15")
        assertTrue(commandOneParameter is InvalidCommand)
    }

    @Test
    fun snowball_valid() {
        val command = parser.parseCommand("${CommandStrings.SNOWBALL.string} 15 13")
        assertTrue(command is SnowballCommand)
        assertEquals(Point(15, 13), command.target)
    }

    @Test
    fun snowball_invalid() {
        val command = parser.parseCommand("${CommandStrings.SNOWBALL.string} A 5")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun snowball_tooShort() {
        val commandOnly = parser.parseCommand("${CommandStrings.SNOWBALL.string}")
        assertTrue(commandOnly is InvalidCommand)

        val commandOneParameter = parser.parseCommand("${CommandStrings.SNOWBALL.string} 15")
        assertTrue(commandOneParameter is InvalidCommand)
    }

    @Test
    fun nothing() {
        val command = parser.parseCommand("${CommandStrings.NOTHING.string}")
        assertTrue(command is DoNothingCommand)
    }

    @Test
    fun unknown() {
        val command = parser.parseCommand("unknown")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun emptyString() {
        val command = parser.parseCommand("")
        assertTrue(command is InvalidCommand)
    }
}
