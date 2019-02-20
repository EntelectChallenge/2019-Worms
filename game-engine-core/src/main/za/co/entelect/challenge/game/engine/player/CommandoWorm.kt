package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.engine.entities.GameConfig
import za.co.entelect.challenge.game.engine.map.Point

class CommandoWorm(health: Int, position: Point) : Worm(health, position) {

    companion object {
        fun build(config: GameConfig, position: Point): CommandoWorm {
            return CommandoWorm(config.commandoWorms.initialHp, position)
        }
    }

}