package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.engine.entities.GameConfig

class WormsPlayer(val id: Int, val worms: List<Worm>) {

    var currentWorm: Worm = worms[0]
        private set

    init {
        this.worms.forEach { it.player = this }
    }

    val health: Int
        get() = livingWorms.sumBy { it.health }

    val dead
        get() = worms.all { it.dead }

    private val livingWorms
        get() = worms.filter { !it.dead }

    var score: Int = 0
    var doNothingsCount = 0

    fun selectNextWorm() {
        //Assign living worms to a local variable since it is a computed property
        val livingWorms = this.livingWorms
        if (livingWorms.isNotEmpty()) {
            val nextIndex = (livingWorms.indexOf(currentWorm) + 1) % livingWorms.size
            currentWorm = livingWorms[nextIndex]
        }
    }

    companion object {

        /**
         * Build a WormsPlayer with the correct properties from the config
         * @param id A unique identifier for this player
         * @param config The game config object
         */
        fun build(id: Int, config: GameConfig): WormsPlayer {
            val commandoWorms = (0 until config.commandoWorms.count)
                    .map { CommandoWorm.build(config) }

            return WormsPlayer(id, commandoWorms)
        }

    }
}
