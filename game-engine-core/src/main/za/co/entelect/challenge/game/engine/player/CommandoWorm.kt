package za.co.entelect.challenge.game.engine.player

import za.co.entelect.challenge.game.engine.entities.GameConfig
import za.co.entelect.challenge.game.engine.map.Point

//TODO: Should this even be a subclass of Worm or is a factory sufficient?
class CommandoWorm private constructor(health: Int, position: Point, weapon: Weapon) : Worm(0, health,
        position, weapon) {

    companion object {
        fun build(config: GameConfig, position: Point): CommandoWorm {
            return CommandoWorm(config.commandoWorms.initialHp, position, config.commandoWorms.weapon)
        }
    }

}
