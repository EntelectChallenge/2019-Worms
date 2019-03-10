package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.engine.entities.GameConfig

class WormsPlayer(val id: Int, val worms: List<Worm>) {

    init {
        playerCount++
    }

    constructor(worms: List<Worm>): this(playerCount, worms)

    var currentWorm: Worm = worms[0]

    val health: Int
        get() = worms.filter { !it.dead }.sumBy { it.health }

    val dead
        get() = worms.all { it.dead }

    var score: Int = 0

    companion object {
        private var playerCount = 0

        fun construct(config: GameConfig): WormsPlayer {

            TODO("Not Implemented")
        }
    }

    init {
        this.worms.forEach { it.player = this }
    }

}
