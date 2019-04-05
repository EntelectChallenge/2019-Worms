package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.engine.config.GameConfig
import kotlin.js.JsName
import kotlin.jvm.Transient

class WormsPlayer private constructor(val id: Int,
                                      val worms: List<Worm>,
                                      @Transient private val config: GameConfig) {

    @Transient
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

    /**
     * Amount of consecutive rounds the player has done nothing
     */
    @Transient
    var consecutiveDoNothingsCount = 0

    val disqualified
        get() = consecutiveDoNothingsCount > config.maxDoNothings


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
        @JsName("build")
        fun build(id: Int, config: GameConfig): WormsPlayer {
            val commandoWorms = (0 until config.commandoWorms.count)
                    .map { i -> CommandoWorm.build(i, config) }

            return WormsPlayer(id, commandoWorms, config)
        }

        @JsName("buildWithWorms")
        fun build(id: Int, worms: List<Worm>, config: GameConfig): WormsPlayer {
            return WormsPlayer(id, worms, config)
        }

    }
}
