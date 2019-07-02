package za.co.entelect.challenge.game.engine.factory

import mu.KotlinLogging
import za.co.entelect.challenge.game.engine.command.CommandStrings
import za.co.entelect.challenge.game.engine.command.WormsCommand
import za.co.entelect.challenge.game.engine.command.implementation.*
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.Point
import kotlin.random.Random

/**
 * Parses string commands into executable command classes
 * @param commandRandom The [Random] instance to injected into commands that require it
 */
class CommandParser(private val commandRandom: Random, private val config: GameConfig) {

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
        logger.info { "Parsing command \"$rawCommand\"" }

        val splitCommand = rawCommand.split(" ", limit = 4)

        return when (splitCommand[0].toLowerCase()) {
            CommandStrings.MOVE.string -> teleportCommand(splitCommand)
            CommandStrings.DIG.string -> digCommand(splitCommand)
            CommandStrings.SHOOT.string -> shootCommand(splitCommand)
            CommandStrings.BANANA.string -> bananaCommand(splitCommand)
            CommandStrings.SELECT.string -> selectCommand(splitCommand)
            CommandStrings.NOTHING.string -> DoNothingCommand(config)
            else -> InvalidCommand("Unknown command: $rawCommand")
        }
    }

    private fun bananaCommand(splitCommand: List<String>): WormsCommand {
        if (splitCommand.size != 3) {
            return InvalidCommand("Cannot parse banana command: Invalid length ${splitCommand.size}, expected 3")
        }

        val x = splitCommand[1].toIntOrNull()
        val y = splitCommand[2].toIntOrNull()

        return when {
            x == null || y == null -> InvalidCommand("Cannot parse coordinates: Invalid coordinate x:$x y:$y")
            else -> BananaCommand(Point(x, y), config)
        }
    }

    private fun selectCommand(splitCommand: List<String>): WormsCommand {
        if (splitCommand.size != 2) {
            return InvalidCommand("Cannot parse select command: Invalid length ${splitCommand.size}, expected 2")
        }

        val wormId = splitCommand[1].toIntOrNull()

        return if (wormId == null) {
            InvalidCommand("Cannot parse worm Id as a number: ${splitCommand[1]}")
        } else {
            SelectCommand(wormId)
        }
    }

    private fun shootCommand(splitCommand: List<String>): WormsCommand {
        if (splitCommand.size != 2) {
            return InvalidCommand("Cannot parse move command: Invalid length ${splitCommand.size}, expected 2")
        }

        val direction = splitCommand[1]

        return if (!Direction.containsShortened(direction)) {
            InvalidCommand("Cannot parse direction command: Invalid direction $direction")
        } else {
            ShootCommand(Direction.fromShortened(direction), config)
        }
    }

    private fun digCommand(splitCommand: List<String>): WormsCommand {
        if (splitCommand.size != 3) {
            return InvalidCommand("Cannot parse dig command: Invalid length ${splitCommand.size}, expected 3")
        }

        val x = splitCommand[1].toIntOrNull()
        val y = splitCommand[2].toIntOrNull()

        if (x == null || y == null) {
            return InvalidCommand("Cannot parse dig coordinates as numbers: ${splitCommand.subList(1, 2)}")
        }

        return DigCommand(x, y, config)
    }

    private fun teleportCommand(splitCommand: List<String>): WormsCommand {
        if (splitCommand.size != 3) {
            return InvalidCommand("Cannot parse move command: Invalid length ${splitCommand.size}, expected 3")
        }

        val x = splitCommand[1].toIntOrNull()
        val y = splitCommand[2].toIntOrNull()

        if (x == null || y == null) {
            return InvalidCommand("Cannot parse move coordinates as numbers: ${splitCommand.subList(1, 2)}")
        }

        return TeleportCommand(x, y, commandRandom, config)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
