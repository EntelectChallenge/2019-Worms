package za.co.entelect.challenge.game.delegate.renderer

import com.google.gson.Gson
import za.co.entelect.challenge.game.contracts.game.GamePlayer
import za.co.entelect.challenge.game.contracts.renderer.RendererType
import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.MapCell
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.Worm
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.powerups.HealthPack
import java.lang.Exception


class WormsRenderer(private val config: GameConfig, private val rendererType: RendererType) {

    private val gson = Gson()
    private val EOL = System.getProperty("line.separator")

    fun commandPrompt(wormsPlayer: WormsPlayer): String {
        return "Player ${wormsPlayer.id}, enter a command (move x y)/(dig x y)/(shoot U/UR/R/DR/D/DL/L/UL)/(nothing)"
    }

    fun render(wormsMap: WormsMap, player: WormsPlayer): String {
        return when (rendererType) {
            RendererType.TEXT -> renderText(wormsMap, player)
            RendererType.JSON -> renderJson(wormsMap, player)
            RendererType.CONSOLE -> renderConsole(wormsMap, player)
        }
    }

    private fun renderText(wormsMap: WormsMap, player: WormsPlayer): String {
        val wormGameDetails = WormsGameDetails(config, wormsMap, player)

        val matchDetails = """
            |@01 Match Details
            |Current round: ${wormGameDetails.currentRound}
            |Max rounds: ${wormGameDetails.maxRounds}
            |Map size: ${wormGameDetails.mapSize}
            |Players count: ${wormsMap.players.size}
            |Worms count: ${wormsMap.players.first().worms.size}
            """.trimMargin()

        val selfPlayerWorms = wormGameDetails.selfPlayer
                .worms
                .fold("") { sum, worm ->
                    sum + """
                        ${getBaseWormText(worm)}
                        |Digging range: ${worm.diggingRange}
                        |Movement range: ${worm.movementRange}
                        |Weapon damage: ${worm.weapon.damage}
                        |Weapon range: ${worm.weapon.range}
                        """.trimMargin()
                }

        val selfPlayer = """
            |@02 Self Player
            ${getBasePlayerText(wormGameDetails.selfPlayer)}
            |Consecutive do nothings count: ${wormGameDetails.selfPlayer.consecutiveDoNothingsCount}
            |Current worm id: ${wormGameDetails.currentWormId}
            |Worms: $selfPlayerWorms
            """.trimMargin()

        val enemyPlayers = wormGameDetails.enemyPlayers
                .fold("@03 Enemy Players$EOL") { pSum, enemyPlayer ->
                    val worms = enemyPlayer.worms.fold("") { wSum, worm ->
                        wSum + """
                            ${getBaseWormText(worm)}
                            """.trimMargin()
                    }

                    pSum + """
                        ${getBasePlayerText(enemyPlayer)}
                        |Worms: $worms
                        |$EOL """.trimMargin()
                }

        val legend = """
            |@04 Legend
            |DEEP_SPACE: ${CellType.DEEP_SPACE.printable}
            |DIRT: ${CellType.DIRT.printable}
            |AIR: ${CellType.AIR.printable}
            |HEALTH_PACK: ${HealthPack.PRINTABLE}
            |WORM_MARKERS: PlayerId WormId
            """.trimMargin()

        val map = """
            |@05 World Map
            |${getStringMap(wormGameDetails.map)}
            """.trimMargin()

        return """
            |$matchDetails
            |
            |$selfPlayer
            |
            |$enemyPlayers
            |$legend
            |
            |$map
            """.trimMargin()
    }

    private fun getBasePlayerText(enemyPlayer: WormsPlayer): String {
        return """|Player id: ${enemyPlayer.id}
                  |Health: ${enemyPlayer.health}
                  |Score: ${enemyPlayer.score}"""
    }

    private fun getBaseWormText(worm: Worm): String {
        return """|$EOL
                  |Worm id: ${worm.id}
                  |Dead: ${worm.dead}
                  |Health: ${worm.health}
                  |Position x: ${worm.position.x}
                  |Position y: ${worm.position.y}"""
    }

    private fun renderJson(wormsMap: WormsMap, player: WormsPlayer): String {
        val wormGameDetails = WormsGameDetails(config, wormsMap, player)

        return gson.toJson(wormGameDetails)
    }

    private fun renderConsole(wormsMap: WormsMap, player: WormsPlayer): String {
        val wormGameDetails = WormsGameDetails(config, wormsMap, player)
        val selfPlayer = "Self:     H=${wormGameDetails.selfPlayer.health} S=${wormGameDetails.selfPlayer.score} " +
                "W=${wormGameDetails.currentWormId}"

        val enemyPlayers = wormGameDetails.enemyPlayers.fold("") { sum, p ->
            sum + "Player ${p.id}: H=${p.health} S=${p.score}" + EOL
        }

        val header = """
            |$selfPlayer
            |$enemyPlayers
            """.trimMargin()
        val map = getStringMap(wormGameDetails.map)

        return """
            |$header
            |$map
            """.trimMargin()
    }

    private fun getStringMap(arrayMap: List<List<MapCell>>): String {
        return arrayMap.joinToString(EOL) {
            it.joinToString("") { cell ->
                when {
                    cell.powerup != null -> cell.powerup?.printable.toString()
                    cell.occupier != null -> cell.occupier?.printable.toString()
                    else -> cell.type.printable
                }
            }
        }
    }

}
