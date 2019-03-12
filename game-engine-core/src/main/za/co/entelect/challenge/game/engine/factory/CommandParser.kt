package za.co.entelect.challenge.game.engine.factory

import za.co.entelect.challenge.game.engine.command.*
import za.co.entelect.challenge.game.engine.entities.Direction
import kotlin.random.Random

/**
 * Parses string commands into executable command classes
 * @param commandRandom The [Random] instance to injected into commands that require it
 */
class CommandParser(private val commandRandom: Random) {

    /**
     * Parses a command from a string
     * @param rawCommand The command string in one of the following formats:
     *  - `move x y` (Move to a cell)
     *  - `dig x y` (Dig a cell)
     *  - `shoot direction` (Shoot in a direction)
     *  - `nothing` (Do nothing)
     *
     * @return The parsed command or an [InvalidCommand] if the command could not be parsed properly
     */
    fun parseCommand(rawCommand: String): WormsCommand {
        val splitCommand = rawCommand.split(" ")
        val identifier = splitCommand[0].toLowerCase()

        return when (identifier) {
            "move" -> teleportCommand(splitCommand)
            "dig" -> digCommand(splitCommand)
            "shoot" -> shootCommand(splitCommand)
            "nothing" -> DoNothingCommand()
            else -> InvalidCommand("Unknown command ${splitCommand[0]}")
        }
    }

    private fun shootCommand(splitCommand: List<String>): WormsCommand {
        if (splitCommand.size != 2) {
            return InvalidCommand("Cannot parse move command: Invalid length ${splitCommand.size}, expected 2")
        }

        val direction = splitCommand[1]

        if (!Direction.containsShortened(direction)) {
            return InvalidCommand("Cannot parse direction command: Invalid direction $direction")
        }

        return ShootCommand(Direction.fromShortened(direction))
    }

    private fun digCommand(splitCommand: List<String>): WormsCommand {
        if (splitCommand.size != 3) {
            return InvalidCommand("Cannot parse move command: Invalid length ${splitCommand.size}, expected 3")
        }

        val x = splitCommand[1].toIntOrNull()
        val y = splitCommand[2].toIntOrNull()

        if (x == null || y == null) {
            return InvalidCommand("Cannot parse move coordinates as numbers: ${splitCommand.subList(1, 2)}")
        }

        return DigCommand(x, y)
    }

    private fun teleportCommand(splitCommand: List<String>): WormsCommand {
        if (splitCommand.size != 3) {
            return InvalidCommand("Cannot parse dig command: Invalid length ${splitCommand.size}, expected 3")
        }

        val x = splitCommand[1].toIntOrNull()
        val y = splitCommand[2].toIntOrNull()

        if (x == null || y == null) {
            return InvalidCommand("Cannot parse dig coordinates as numbers: ${splitCommand.subList(1, 2)}")
        }

        return TeleportCommand(x, y, commandRandom)
    }
}
