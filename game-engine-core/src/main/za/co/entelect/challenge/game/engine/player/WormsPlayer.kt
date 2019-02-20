package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.engine.entities.GameConfig

class WormsPlayer(val worms: List<Worm>) {

    var consecutiveDoNothings = 0
    var currentWorm: Worm = worms[0]

    val livingWorms
        get() = worms.filter { !it.dead }

    val health: Int
        get() = worms.sumBy { it.health }

    val dead
        get() = worms.all { it.dead }

    var score: Int = 0

    companion object {
        fun construct(config: GameConfig): WormsPlayer {
            config.commandoWorms

            TODO("Not Implemented")
        }
    }
}