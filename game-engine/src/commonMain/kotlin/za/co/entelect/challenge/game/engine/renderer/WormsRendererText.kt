package za.co.entelect.challenge.game.engine.renderer

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.map.CellType
import za.co.entelect.challenge.game.engine.map.WormsMap
import za.co.entelect.challenge.game.engine.player.WormsPlayer
import za.co.entelect.challenge.game.engine.powerups.HealthPack
import za.co.entelect.challenge.game.engine.renderer.WormsRenderer.Companion.EOL
import za.co.entelect.challenge.game.engine.renderer.printables.PrintableMapCell.Companion.getStringMap
import za.co.entelect.challenge.game.engine.renderer.printables.PrintablePlayer
import za.co.entelect.challenge.game.engine.renderer.printables.PrintableWorm

class WormsRendererText(private val config: GameConfig) : WormsRenderer {

    override fun commandPrompt(wormsPlayer: WormsPlayer): String {
        return "Not supported in Text state file"
    }

    override fun render(wormsMap: WormsMap, player: WormsPlayer?): String {
        val wormGameDetails = WormsGameDetails(config, wormsMap, player)

        val matchDetails = """
            |@01 Match Details
            |Current round: ${wormGameDetails.currentRound}
            |Max rounds: ${wormGameDetails.maxRounds}
            |Map size: ${wormGameDetails.mapSize}
            |Current worm id: ${wormGameDetails.currentWormId}
            |Consecutive do nothing count: ${wormGameDetails.consecutiveDoNothingCount}
            |Players count: ${wormsMap.players.size}
            |Worms per player: ${wormsMap.players.first().worms.size}
            |Pushback damage: ${wormGameDetails.pushbackDamage}
            """.trimMargin()

        val myPlayerWorms = wormGameDetails.myPlayer!!.worms
                .fold("") { sum, worm ->
                    sum + """
                        ${getBaseWormText(worm)}
                        ${getWormWeaponDetails(worm)}
                        ${getWormBananasDetails(worm)}
                        """.trimMargin()
                }

        val myPlayer = """
            |@02 My Player
            ${getBasePlayerText(wormGameDetails.myPlayer)}
            |Health: ${wormGameDetails.myPlayer.health}
            |Current Worm: ${wormGameDetails.myPlayer.currentWormId}
            |Worms: $myPlayerWorms
            """.trimMargin()

        val opponentPlayers = wormGameDetails.opponents
                .fold("@03 Opponents$EOL") { pSum, opponentPlayer ->
                    val worms = opponentPlayer.worms.fold("") { wSum, worm ->
                        wSum + """
                            ${getBaseWormText(worm)}
                            """.trimMargin()
                    } + EOL

                    pSum + """
                        ${getBasePlayerText(opponentPlayer)}
                        |Current Worm: ${opponentPlayer.currentWormId}
                        |Worms: $worms
                        |$EOL """.trimMargin()
                }

        val specialItems = """
            |@04 Special Items
            |HEALTH_PACK: ${HealthPack.build(config).value}
            """.trimMargin()

        val legend = """
            |@05 Legend
            |DEEP_SPACE: ${CellType.DEEP_SPACE.printable} ASCII:219
            |DIRT: ${CellType.DIRT.printable} ASCII:178
            |AIR: ${CellType.AIR.printable} ASCII:176
            |HEALTH_PACK: ${HealthPack.PRINTABLE} ASCII:204, 185
            |WORM_MARKER: 13 Example for:Player1, Worm3
            """.trimMargin()

        val map = """
            |@06 World Map
            |${getStringMap(wormGameDetails.map)}
            """.trimMargin()

        return """
            |${addLinesCount(matchDetails)}
            |
            |${addLinesCount(myPlayer)}
            |
            |${addLinesCount(opponentPlayers)}
            |${addLinesCount(specialItems)}
            |
            |${addLinesCount(legend)}
            |
            |${addLinesCount(map)}
            """.trimMargin()
    }

    private fun getWormWeaponDetails(worm: PrintableWorm) =
            """|Weapon damage: ${worm.weapon?.damage}
                            |Weapon range: ${worm.weapon?.range}"""

    private fun getWormBananasDetails(worm: PrintableWorm) =
            """|Banana bomb damage: ${worm.bananaBombs?.damage}
                            |Banana bomb range: ${worm.bananaBombs?.range}
                            |Banana bombs count: ${worm.bananaBombs?.count}
                            |Banana bomb damage radius: ${worm.bananaBombs?.damageRadius}"""

    private fun addLinesCount(section: String): String {
        val lines = section.split(EOL).toMutableList()
        lines.add(1, "Section lines count: ${lines.size + 1}")

        return lines.joinToString(EOL)
    }

    private fun getBasePlayerText(player: PrintablePlayer): String {
        return """|Player id: ${player.id}
                  |Score: ${player.score}
                  |Selection Tokens: ${player.remainingWormSelections}"""
    }

    private fun getBaseWormText(worm: PrintableWorm): String {
        return """|$EOL
                  |Worm id: ${worm.id}
                  |Health: ${worm.health}
                  |Position x: ${worm.position?.x}
                  |Position y: ${worm.position?.y}
                  |Digging range: ${worm.diggingRange}
                  |Movement range: ${worm.movementRange}
                  |Profession: ${worm.profession}"""
    }

}
