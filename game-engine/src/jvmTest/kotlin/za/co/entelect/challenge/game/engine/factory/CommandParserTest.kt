package za.co.entelect.challenge.game.engine.factory

import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
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
        val command = parser.parseCommand("move 3 4")
        assertTrue(command is TeleportCommand)
        assertEquals(Point(3, 4), command.target)
    }

    @Test
    fun move_invalidNumber_x() {
        val command = parser.parseCommand("move 3 a")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun move_invalidNumber_y() {
        val command = parser.parseCommand("move c 2")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun move_invalidNumber_both() {
        val command = parser.parseCommand("move e f")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun move_tooShort() {
        val command = parser.parseCommand("move 3")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun dig_valid() {
        val command = parser.parseCommand("dig 2 1")
        assertTrue(command is DigCommand)
        assertEquals(Point(2, 1), command.target)
    }

    @Test
    fun dig_invalidNumber_x() {
        val command = parser.parseCommand("dig b 4")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun dig_invalidNumber_y() {
        val command = parser.parseCommand("dig 3 a")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun dig_invalidNumber_both() {
        val command = parser.parseCommand("dig x y")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun dig_tooShort() {
        val command = parser.parseCommand("dig")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun shoot_valid() {
        val command = parser.parseCommand("shoot NW")
        assertTrue(command is ShootCommand)
        assertEquals(Direction.UP_LEFT, command.direction)
    }

    @Test
    fun shoot_invalid() {
        val command = parser.parseCommand("shoot TEST")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun shoot_tooShort() {
        val command = parser.parseCommand("shoot")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun select_valid() {
        val command = parser.parseCommand("select 1")
        assertTrue(command is SelectCommand)
        assertEquals(1, command.wormId)
    }

    @Test
    fun select_invalid() {
        val command = parser.parseCommand("select A")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun select_tooShort() {
        val command = parser.parseCommand("select")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun banana_valid() {
        val command = parser.parseCommand("banana 15 13")
        assertTrue(command is BananaCommand)
        assertEquals(Point(15, 13), command.target)
    }

    @Test
    fun banana_invalid() {
        val command = parser.parseCommand("banana A 5")
        assertTrue(command is InvalidCommand)
    }

    @Test
    fun banana_tooShort() {
        val commandOnly = parser.parseCommand("banana")
        assertTrue(commandOnly is InvalidCommand)

        val commandOneParameter = parser.parseCommand("banana 15")
        assertTrue(commandOneParameter is InvalidCommand)
    }

    @Test
    fun nothing() {
        val command = parser.parseCommand("nothing")
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
