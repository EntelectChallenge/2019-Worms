package za.co.entelect.challenge.game.delegate.renderer

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import za.co.entelect.challenge.game.delegate.factory.TEST_CONFIG
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.factory.TestMapFactory.buildMapWithCellType
import za.co.entelect.challenge.game.engine.factory.TestWormsPlayerFactory.buildWormsPlayerDefault
import za.co.entelect.challenge.game.engine.factory.TestWormsPlayerFactory.buildWormsPlayers
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.Point
import za.co.entelect.challenge.game.engine.map.WormsMapGenerator
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.powerups.HealthPack
import za.co.entelect.challenge.game.engine.renderer.WormsRendererConsole
import za.co.entelect.challenge.game.engine.renderer.WormsRendererJson
import za.co.entelect.challenge.game.engine.renderer.WormsRendererText
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WormsRendererTest {

    private val config: GameConfig = TEST_CONFIG

    @Test
    fun test_rendered_output_matches() {
        val wormsPlayers = buildWormsPlayerDefault(config)
        val player1 = wormsPlayers.first()

        wormsPlayers.forEachIndexed { i, p ->
            p.worms.forEachIndexed { j, w ->
                w.initPositions(Point(i, j))
            }
        }

        val lightPixel = CellType.AIR
        val darkPixel = CellType.DEEP_SPACE
        val wormsMap = buildMapWithCellType(wormsPlayers, config.mapSize, lightPixel)

        wormsMap[2, 0].type = CellType.AIR
        wormsMap[2, 1].type = CellType.DIRT
        wormsMap[2, 2].type = CellType.DEEP_SPACE
        wormsMap[2, 3].powerup = HealthPack(config.healthPackHp)

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

        val rendererText = WormsRendererText(config)
        val rendererJson = WormsRendererJson(config)
        val rendererConsole = WormsRendererConsole(config)

        val textFileString = rendererText.render(wormsMap, player1)
        val jsonFileString = rendererJson.render(wormsMap, player1)
        val consoleFileString = rendererConsole.render(wormsMap, player1)

        val mapLines = textFileString.lines()
        val mapHeaderLineNumber = mapLines.indexOfFirst { it.contains("@06") }
        assertTrue(mapLines[mapHeaderLineNumber + 2].startsWith("1121" + CellType.AIR.printable)
                && mapLines[mapHeaderLineNumber + 3].startsWith("1222" + CellType.DIRT.printable)
                && mapLines[mapHeaderLineNumber + 4].startsWith("1323" + CellType.DEEP_SPACE.printable)
                && mapLines[mapHeaderLineNumber + 5].contains(HealthPack.PRINTABLE),
                "Text state file has a bad map render. " +
                        "Printed map does not contain the expected worm markers, cell types and powerups")

        assertTrue(upSign.lines()
                .mapIndexed { lineNumber, lineText -> Pair(lineNumber, lineText) }
                .all { (index, text) -> mapLines[mapHeaderLineNumber + 6 + index].contains(getPixelfiedString(text, o, i)) },
                "Text state file has a bad map render. " +
                        "The signage 'UP' was not found on the rendered map where expected. " +
                        "Check if map rotation/flip is correct, or that 'World Map' header is correct")

        val jsonPropertiesShouldExist = listOf(
                "currentRound",
                "maxRounds",
                "pushbackDamage",
                "mapSize",
                "currentWormId",
                "consecutiveDoNothingCount",
                "myPlayer",
                "id",
                "score",
                "health",
                "worms",
                "position",
                "x",
                "y",
                "health",
                "diggingRange",
                "movementRange",
                "profession",
                "bananaBombs",
                "damage",
                "range",
                "count",
                "damageRadius",
                "powerup",
                "type",
                "value",
                CellType.AIR.name,
                CellType.DIRT.name,
                CellType.DEEP_SPACE.name)
        val propertiesNotFound = jsonPropertiesShouldExist.filter { prop ->
            val index = jsonFileString.lines().indexOfFirst { it.contains(prop) }
            index == -1
        }
        assertTrue(propertiesNotFound.isEmpty(), "JSON state file is missing some properties >> " +
                "[" + propertiesNotFound.joinToString(separator = ", ") + "]")

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
        val consoleRenderer = WormsRendererConsole(config)
        val commandPrompt = consoleRenderer.commandPrompt(player)

        assertNotNull(commandPrompt, "Console command prompt is not supposed to be null")
    }

    /**
     * Creates example state files from each renderer when a build is triggered
     * Use IDE debugging at the end of this function to retrieve examples of map files
     * This should be used to keep new starter-pack release up to date
     */
    @Test
    fun test_print_example_map_files() {
        val wormsPlayers = buildWormsPlayerDefault(config)
        val player1 = wormsPlayers.first()

        val wormsMapGenerator = WormsMapGenerator(config, 0)
        val wormsMap = wormsMapGenerator.getMap(wormsPlayers)

        val rendererText = WormsRendererText(config)
        val rendererJson = WormsRendererJson(config)
        val rendererConsole = WormsRendererConsole(config)

        val textFileString = rendererText.render(wormsMap, player1)
        val jsonFileString = rendererJson.render(wormsMap, player1)
        val consoleFileString = rendererConsole.render(wormsMap, player1)

        val PATH = "assets/example-state"
        try {
            File(PATH).mkdirs()

            writeFile(PATH, "state.txt", textFileString)

            val prettyJson = GsonBuilder().setPrettyPrinting().create().toJson(JsonParser().parse(jsonFileString).asJsonObject)
            writeFile(PATH, "state.json", prettyJson)

            writeFile(PATH, "console.txt", consoleFileString)

        } catch (e: IOException) {
            throw e
        }
    }

    private fun writeFile(path: String, fileName: String, content: String) {
        val fileOutputStream = FileOutputStream(File(path, fileName))
        val outputStreamWriter = OutputStreamWriter(fileOutputStream, Charsets.UTF_8)

        outputStreamWriter.use {
            it.write(content)
        }
    }

    @Test
    fun testCommandPrompt() {
        val rendererText = WormsRendererText(config)
        val rendererJson = WormsRendererJson(config)
        val rendererConsole = WormsRendererConsole(config)

        val wormsPlayer = WormsPlayer.build(1, config)

        assertTrue(rendererText.commandPrompt(wormsPlayer).toLowerCase().contains("not supported"))
        assertTrue(rendererJson.commandPrompt(wormsPlayer).toLowerCase().contains("not supported"))
        assertFalse(rendererConsole.commandPrompt(wormsPlayer).toLowerCase().contains("not supported"))
    }

}
