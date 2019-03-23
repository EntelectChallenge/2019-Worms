package za.co.entelect.challenge.game.delegate.renderer

import za.co.entelect.challenge.game.contracts.renderer.RendererType
import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.factory.TestMapFactory.buildMapWithCellType
import za.co.entelect.challenge.game.engine.factory.TestWormsPlayerFactory.buildWormsPlayers
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.powerups.HealthPack
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WormsRendererTest {

    private val config: GameConfig = TEST_CONFIG

    @Test
    fun test_rendered_output_matches() {
        val wormsPlayers = buildWormsPlayers(config, 3, 3)
        val player1 = wormsPlayers.first()

        wormsPlayers.forEachIndexed { i, p ->
            p.worms.forEachIndexed { j, w ->
                w.initPositions(Point(i, j))
            }
        }

        val lightPixel = CellType.AIR
        val darkPixel = CellType.DEEP_SPACE
        val wormsMap = buildMapWithCellType(wormsPlayers, config.mapSize, lightPixel)

        wormsMap[3, 0].type = CellType.AIR
        wormsMap[3, 1].type = CellType.DIRT
        wormsMap[3, 2].type = CellType.DEEP_SPACE
        wormsMap[3, 3].powerup = HealthPack(config.healthPackHp)

        // Shows the real "up" side of the map
        val i = lightPixel.printable
        val o = darkPixel.printable
        val upSign = """
            |█░█░███
            |█░█░█░█
            |█░█░███
            |█░█░█░░
            |███░█░░
            """.trimMargin()
        val upSignPoints = upSign.lines()
                .mapIndexed { y, line ->
                    line.trim().split("")
                            .mapIndexed { x, char -> if (char != "█") Point(-1, -1) else Point(x, y) }
                            .filter { it != Point(-1, -1) }
                }.flatten()
                .map { (x, y) -> Point(x + 5, y + 4) }
        upSignPoints.forEach { wormsMap[it].type = darkPixel }

        val rendererText = WormsRenderer(config, RendererType.TEXT)
        val rendererJson = WormsRenderer(config, RendererType.JSON)
        val rendererConsole = WormsRenderer(config, RendererType.CONSOLE)

        val textFileString = rendererText.render(wormsMap, player1)
        val jsonFileString = rendererJson.render(wormsMap, player1)
        val consoleFileString = rendererConsole.render(wormsMap, player1)

        val mapLines = textFileString.lines()
        val mapHeaderLineNumber = mapLines.indexOfFirst { it.contains("@05") }
        assertTrue(mapLines[mapHeaderLineNumber + 1].startsWith("112131" + CellType.AIR.printable)
                && mapLines[mapHeaderLineNumber + 2].startsWith("122232" + CellType.DIRT.printable)
                && mapLines[mapHeaderLineNumber + 3].startsWith("132333" + CellType.DEEP_SPACE.printable)
                && mapLines[mapHeaderLineNumber + 4].contains(HealthPack.PRINTABLE),
                "Text state file has a bad map render. " +
                        "Printed map does not contain the expected worm markers, cell types and powerups")

        assertTrue(upSign.lines()
                .mapIndexed { lineNumber, lineText -> Pair(lineNumber, lineText) }
                .all { (index, text) -> mapLines[mapHeaderLineNumber + 5 + index].contains(getPixelfiedString(text, o, i)) },
                "Text state file has a bad map render. " +
                        "The signage 'UP' was not found on the rendered map where expected. " +
                        "Check if map rotation/flip is correct, or that 'World Map' header is correct")

        val jsonPropertiesShouldExist = listOf(
                "currentRound",
                "maxRounds",
                "mapSize",
                "currentWormId",
                "selfPlayer",
                "score",
                "id",
                "position",
                "x",
                "y",
                "health",
                "diggingRange",
                "movementRange",
                CellType.AIR.name,
                CellType.DIRT.name,
                CellType.DEEP_SPACE.name)
        val propertiesNotFound = jsonPropertiesShouldExist.filter { prop ->
            val index = jsonFileString.lines().indexOfFirst { it.contains(prop) }
            index == -1
        }
        assertTrue(propertiesNotFound.isEmpty(), "JSON state file is missing some properties. " +
                propertiesNotFound.fold("These were not found: ") { sum, s -> "$sum$s, " })

        assertTrue(consoleFileString.lines().size > wormsMap.size, "Console state file is too short. " +
                "Check if the rendered map is missing")
    }

    private fun getPixelfiedString(lineText: String, darkPixel: String, lightPixel: String) =
            lineText.trim().split("")
                    .map { if (it == "█") darkPixel else lightPixel }
                    .joinToString("")

    @Test
    fun test_command_prompt() {
        val player = buildWormsPlayers(config, 1, 1)[0]
        val consoleRenderer = WormsRenderer(config, RendererType.CONSOLE)
        val commandPrompt = consoleRenderer.commandPrompt(player)

        assertNotNull(commandPrompt, "Console command prompt is not supposed to be null")
    }

}
