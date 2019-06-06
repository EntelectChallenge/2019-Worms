package za.co.entelect.challenge.game.engine.factory

import za.co.entelect.challenge.game.engine.config.GameConfig
import za.co.entelect.challenge.game.engine.player.AgentWorm
import za.co.entelect.challenge.game.engine.player.CommandoWorm
import za.co.entelect.challenge.game.engine.player.WormsPlayer

object TestWormsPlayerFactory {

    fun buildWormsPlayers(config: GameConfig, playersCount: Int = 2, wormsCount: Int = config.commandoWorms.count): List<WormsPlayer> {
        val safePlayersCount = if (playersCount < 1) 1 else playersCount
        val safeWormsCount = if (wormsCount < 1) 1 else wormsCount

        return (1..safePlayersCount).map {
            val playerSquad = (1..safeWormsCount).map { wormIndex ->
                CommandoWorm.build(wormIndex, config)
            }
            WormsPlayer.build(it, playerSquad, config)
        }
    }

    fun buildWormsPlayerDefault(config: GameConfig): List<WormsPlayer> {
        return (1..2).map { WormsPlayer.build(it, config) }
    }

}
